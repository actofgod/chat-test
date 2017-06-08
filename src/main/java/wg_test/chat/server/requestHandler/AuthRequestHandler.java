package wg_test.chat.server.requestHandler;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import wg_test.chat.proto.Response;
import wg_test.chat.proto.UserResponse;
import wg_test.chat.proto.AuthRequest;
import wg_test.chat.proto.AuthResponse;
import wg_test.chat.server.*;
import wg_test.chat.server.entity.User;
import wg_test.chat.server.entity.UserToken;
import wg_test.chat.server.service.AuthService;

/**
 * Класс обработчика запросов авторизации
 */
public class AuthRequestHandler extends RequestHandler<AuthRequest.AuthMessage>
{
    /**
     * Сервис авторизации пользователей
     */
    private AuthService authService;

    /**
     * Карта сопоставления контекстов соединений и токенов
     */
    private TokenContextMap ctxMap;

    /**
     * Хранилище токенов
     */
    private TokenStorage tokenStorage;

    public AuthRequestHandler(ServiceLocator locator)
    {
        super(locator);
    }

    @Override
    public Response.ServerMessage.MessageType getResponseType()
    {
        return Response.ServerMessage.MessageType.AUTH;
    }

    @Override
    public Parser<AuthRequest.AuthMessage> getRequestParser()
    {
        return AuthRequest.AuthMessage.parser();
    }

    /**
     * Обрабатвает сообщение аутентификации
     * @param ctx Контекст соединения
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    @Override
    public GeneratedMessageV3 execute(
            ChannelHandlerContext ctx,
            AuthRequest.AuthMessage request,
            Response.ServerMessage.Builder response
    ) {

        // проверяем валидность имени пользователя
        String userName = request.getUsername();
        if (userName == null || userName.length() < 3 || userName.length() >= 64) {
            return handleErrorRequest(response, 4, "Invalid user name");
        }

        // проверяем валидность пароля пользователя
        String password = request.getPassword();
        if (password == null || password.length() >= 128 || password.length() < 4) {
            return handleErrorRequest(response, 4, "Invalid password");
        }

        // проверяем, правильно ли указал пользователь свои имя и пароль
        User user = getAuthService().checkCredentials(userName, password);
        if (user == null) {
            return handleErrorRequest(response, 4, "Wrong username or password");
        }

        // выдаём пользователю токен
        UserToken token = getAuthService().authenticate(user, ctx);
        if (token == null) {
            return handleErrorRequest(response, 5, "Failed to create user token");
        }

        // добавляем в хранилище токенов созданный токен
        getTokenStorage().addToken(token);

        // добавляем в карту сопоставления контекстов соединения и токенов созданный токен
        getTokenContextMap().add(ctx, token);

        // добавляем слушателя, который при дисконнекте пометит пользователя как не активного
        ctx.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                // удаляем связь токена с контекстом соединения
                token.invalidateConnectionContext();
                // удаляем из карты соответствия соединений и токенов закрытый контекст
                getTokenContextMap().remove(ctx);
                // если список активных токено пуст - сообщаем всем, что пользоватль ушел в оффлайн
                if (token.getUser().getActiveTokens().isEmpty()) {
                    sendUserStatusMessage(token.getUser(), false);
                }
            }
        });

        // добавляем в очередь сообщений сообщение о том, что пользователь теперь онлайн
        sendUserStatusMessage(user, true);
        // генерируем ответ пользователю
        response.setSuccess(true);
        return AuthResponse.AuthMessage.newBuilder()
                .setToken(token.getTokenValue())
                .setValidBefore(token.getValidBefore().getTime())
                .setUserId(user.getId())
                .build();
    }

    /**
     * Добавляет в очередь рассылки всем сообщение о том, что пользователь вошёл в сеть или вышел из сети
     * @param user Инстанс пользователя, статус которого изменился
     * @param online Онлайн пользователь или оффлайн
     */
    private void sendUserStatusMessage(User user, boolean online)
    {
        Response.ServerMessage message = Response.ServerMessage.newBuilder()
                .setType(Response.ServerMessage.MessageType.USER_STATUS_CHANGE)
                .setSuccess(true)
                .setData(
                        UserResponse.UserStatusChangeMessage.newBuilder()
                                .setUserId(user.getId())
                                .setOnline(online)
                                .build()
                                .toByteString()
                ).build();
        getServiceLocator().getMessageQueueService().enqueueMessage(message);
    }

    private AuthService getAuthService()
    {
        if (authService == null) {
            authService = getServiceLocator().getAuthService();
        }
        return authService;
    }

    private TokenContextMap getTokenContextMap()
    {
        if (ctxMap == null) {
            ctxMap = getServiceLocator().getTokenContextMap();
        }
        return ctxMap;
    }

    private TokenStorage getTokenStorage()
    {
        if (tokenStorage == null) {
            tokenStorage = getServiceLocator().getTokenStorage();
        }
        return tokenStorage;
    }
}

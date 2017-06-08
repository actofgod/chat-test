package wg_test.chat.server.requestHandler;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.proto.Response;
import wg_test.chat.proto.AuthRequest;
import wg_test.chat.proto.AuthResponse;
import wg_test.chat.proto.UserResponse;
import wg_test.chat.server.*;
import wg_test.chat.server.entity.User;
import wg_test.chat.server.entity.UserToken;
import wg_test.chat.server.service.AuthService;
import wg_test.chat.server.service.PasswordService;
import wg_test.chat.server.service.UserService;
import wg_test.chat.utils.Hex;

/**
 * Класс обработчика запросов на создание нового пользователя
 */
public class RegisterRequestHandler extends RequestHandler<AuthRequest.RegisterMessage>
{
    /**
     * Сервис для работы с пользователями
     */
    private UserService userService;

    /**
     * Сервис актентификации пользователей
     */
    private AuthService authService;

    /**
     * Сервис работы с паролями
     */
    private PasswordService passwordService;

    /**
     * Хранилище токенов
     */
    private TokenStorage tokenStorage;

    public RegisterRequestHandler(ServiceLocator locator)
    {
        super(locator);
    }

    @Override
    public Response.ServerMessage.MessageType getResponseType()
    {
        return Response.ServerMessage.MessageType.REGISTER;
    }

    @Override
    public Parser<AuthRequest.RegisterMessage> getRequestParser()
    {
        return AuthRequest.RegisterMessage.parser();
    }

    /**
     * Обрабатвает запрос на регистрацию нового пользователя
     * @param ctx Контекст соединения
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    @Override
    public GeneratedMessageV3 execute(
            ChannelHandlerContext ctx,
            AuthRequest.RegisterMessage request,
            Response.ServerMessage.Builder response
    ) {
        // валидируем переданное имя пользователя
        String userName = request.getUsername();
        if (userName == null || userName.isEmpty()) {
            return handleErrorRequest(response, 4, "Invalid user name");
        }
        if (userName.length() < 3) {
            return handleErrorRequest(response, 4, "User name to short");
        }
        if (userName.length() >= 64) {
            return handleErrorRequest(response, 4, "User name to long");
        }

        // валидируем переданный пароль
        String password = request.getPassword();
        if (password == null || password.isEmpty()) {
            return handleErrorRequest(response, 4, "Invalid password");
        }
        if (password.length() >= 128) {
            return handleErrorRequest(response, 4, "Password to long");
        }
        if (password.length() < 4) {
            return handleErrorRequest(response, 4, "Password to short");
        }

        User user = getUserService().getUserByName(userName);
        if (user != null) {
            // если пользователь с таким именем уже существует - ошибка
            return handleErrorRequest(response, 4, "User with user name '" + userName + "' already exists");
        }
        // генерируем соль и хэш пароля пользователя
        byte[] salt = getPasswordService().generateSalt();
        byte[] hash = getPasswordService().hashPassword(password, salt);

        // создаём нового пользователя
        user = getUserService().createUser(request.getUsername(), Hex.bytesToHex(hash), Hex.bytesToHex(salt));
        if (user == null) {
            // если создать пользователя не удалось - сообщаем об этом
            return handleErrorRequest(response, 4, "Failed to create new user");
        }

        // авторизируем пользователя
        UserToken token = getAuthService().authenticate(user, ctx);
        if (token == null) {
            // если токен создать не удалось - ошибка
            return handleErrorRequest(response, 5, "Failed to create user token");
        }
        // добавляем токен в хранилище токенов
        getTokenStorage().addToken(token);
        // добавляем токен в карту соответствия контекстов соединений и токенов созданный токен
        getServiceLocator().getTokenContextMap().add(ctx, token);
        // добавляем слушателя, который при разрыве соединения отправит пользователя оффлайн
        ctx.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                // удаляем связь токена с контекстом соединения
                token.invalidateConnectionContext();
                // удаляем из карты соответствия соединений и токенов закрытый контекст
                getServiceLocator().getTokenContextMap().remove(ctx);
                // если список активных токено пуст - сообщаем всем, что пользоватль ушел в оффлайн
                if (token.getUser().getActiveTokens().isEmpty()) {
                    sendUserStatusMessage(token.getUser(), false);
                }
            }
        });
        sendNewUserMessage(user);
        response.setSuccess(true);
        return AuthResponse.RegisterMessage.newBuilder()
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

    /**
     * Отправляет сообщение о создании нового пользователя всем активным пользователям
     * @param user Инстанс созданного пользователя
     */
    private void sendNewUserMessage(User user)
    {
        Response.ServerMessage message = Response.ServerMessage.newBuilder()
                .setSuccess(true)
                .setType(Response.ServerMessage.MessageType.NEW_USER)
                .setData(
                        UserResponse.User.newBuilder()
                                .setId(user.getId())
                                .setUsername(user.getUserName())
                                .setOnline(true)
                                .build()
                                .toByteString()
                ).build();
        getServiceLocator().getMessageQueueService().enqueueMessage(message);
    }

    private UserService getUserService()
    {
        if (userService == null) {
            userService = getServiceLocator().getUserService();
        }
        return userService;
    }

    private AuthService getAuthService()
    {
        if (authService == null) {
            authService = getServiceLocator().getAuthService();
        }
        return authService;
    }

    private PasswordService getPasswordService()
    {
        if (passwordService == null) {
            passwordService = getServiceLocator().getPasswordService();
        }
        return passwordService;
    }

    private TokenStorage getTokenStorage()
    {
        if (tokenStorage == null) {
            tokenStorage = getServiceLocator().getTokenStorage();
        }
        return tokenStorage;
    }
}

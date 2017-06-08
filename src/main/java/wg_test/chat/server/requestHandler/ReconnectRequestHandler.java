package wg_test.chat.server.requestHandler;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.proto.AuthRequest;
import wg_test.chat.proto.AuthResponse;
import wg_test.chat.proto.Response;
import wg_test.chat.server.*;
import wg_test.chat.server.entity.UserToken;
import wg_test.chat.server.service.AuthService;

import java.util.Date;

/**
 * Обработчик запросов на реконнект клиента
 */
public class ReconnectRequestHandler extends RequestHandler<AuthRequest.ReconnectMessage>
{
    /**
     * Сервис аутентификации
     */
    private AuthService authService;

    /**
     * Карта соотвествия контекстов соединений и токенов пользователей
     */
    private TokenContextMap ctxMap;

    /**
     * Хранилище токенов
     */
    private TokenStorage tokenStorage;

    public ReconnectRequestHandler(ServiceLocator locator)
    {
        super(locator);
    }

    @Override
    public Response.ServerMessage.MessageType getResponseType()
    {
        return Response.ServerMessage.MessageType.VALIDATE_TOKEN;
    }

    @Override
    public Parser<AuthRequest.ReconnectMessage> getRequestParser()
    {
        return AuthRequest.ReconnectMessage.parser();
    }

    /**
     * Обрабатвает запрос на переподключение с существующим токеном
     * @param ctx Контекст соединения
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    @Override
    public GeneratedMessageV3 execute(
            ChannelHandlerContext ctx,
            AuthRequest.ReconnectMessage request,
            Response.ServerMessage.Builder response
    ) {
        // проверяем валидность значения токена
        String tokenValue = request.getToken();
        if (tokenValue == null || tokenValue.length() < 16) {
            return handleErrorRequest(response, 4, "Invalid token value");
        }

        // прверяем, не активен ли случайно токен в данный момент
        UserToken token = getTokenContextMap().getTokenByContext(ctx);
        if (token != null) {
            return handleErrorRequest(response, 4, "Invalid reconnect request");
        }

        // получаем айди пользователя и дату до которой токен валиден
        int userId = getTokenStorage().getTokenUserId(tokenValue);
        if (userId <= 0) {
            return handleErrorRequest(response, 4, "Token expired, authentication required");
        }
        Date validBefore = getTokenStorage().getTokenValidBefore(tokenValue);
        if (validBefore == null) {
            return handleErrorRequest(response, 4, "Token expired, authentication required");
        }

        // заново авторизуем пользователя
        token = getAuthService().reAuthenticate(tokenValue, userId, validBefore, ctx);
        if (token == null) {
            return handleErrorRequest(response, 4, "Failed to re-authenticate token");
        }
        return AuthResponse.ReconnectMessage.newBuilder()
                .setToken(token.getTokenValue())
                .build();
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

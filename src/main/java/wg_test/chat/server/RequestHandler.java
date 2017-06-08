package wg_test.chat.server;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.proto.ErrorInfo;
import wg_test.chat.proto.Response;
import wg_test.chat.server.entity.UserToken;

/**
 * Класс обработчика сообщений с клиента
 * @param <TRequestType> Тип обрабатываемого сообщения
 */
public abstract class RequestHandler<TRequestType extends GeneratedMessageV3>
{
    /**
     * Сервис локатор приложения
     */
    private ServiceLocator locator;

    public RequestHandler(ServiceLocator serviceLocator)
    {
        locator = serviceLocator;
    }

    /**
     * Проверяет, требуется ли, чтоб запрос осуществлялся из контекста авторизованного пользвателя
     * @return True если требуется проверка авторизации пользователя, false если нет
     */
    public boolean isTokenRequired()
    {
        return false;
    }

    /**
     * Проверяет наличие валидного токена в текущем контексте соединения
     * @param ctx Контекст соединенеия клиента с сервером
     * @return True если пользователь авторизован, false если нет
     */
    public boolean validateToken(ChannelHandlerContext ctx)
    {
        if (isTokenRequired()) {
            UserToken token = getServiceLocator().getTokenContextMap().getTokenByContext(ctx);
            if (token == null) {
                return false;
            }
            if (token.getValidBefore().getTime() - System.currentTimeMillis() <= 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Возвращает сервис локатор приложения
     * @return Сервис локатор
     */
    protected ServiceLocator getServiceLocator()
    {
        return locator;
    }

    /**
     * Возвращает сообщение об ошибке для отправи клиенту
     * @param response Инстанс обрабатываемого запроса с клиента
     * @param code Код произошедшей ошибки
     * @param message Текст с описанием ошибки
     * @return Инстанс сообщения для отправки клиенту
     */
    protected GeneratedMessageV3 handleErrorRequest(Response.ServerMessage.Builder response, int code, String message)
    {
        response.setSuccess(false);
        return ErrorInfo.Error.newBuilder().setCode(code).setReason(message).build();
    }

    /**
     * Возвращает тип, устанавливаемый в ответе клиенту при обработке запроса текущим обработчиком
     * @return Тип ответа клиенту
     */
    abstract public Response.ServerMessage.MessageType getResponseType();

    /**
     * Возвращает парсер сообщения от клиента
     * @return Парсер сообщения
     */
    abstract public Parser<TRequestType> getRequestParser();

    /**
     * Обрабатвает запрос от клиента
     * @param ctx Контекст соединения
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    abstract public GeneratedMessageV3 execute(
            ChannelHandlerContext ctx,
            TRequestType request,
            Response.ServerMessage.Builder response
    );
}

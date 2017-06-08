package wg_test.chat.server;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.proto.Request;
import wg_test.chat.proto.Response;
import wg_test.chat.server.RequestHandler;

/**
 * Класс отдельно взятого роута
 * @param <TRequestType> Тип обрабатываемых сообщений от клиента
 */
public class Route<TRequestType extends GeneratedMessageV3>
{
    /**
     * Обработчик сообщений
     */
    private RequestHandler<TRequestType> handler;

    /**
     * Парсер входящих сообщений
     */
    private Parser<TRequestType> dataParser;

    public Route(RequestHandler<TRequestType> handler)
    {
        this.handler = handler;
        this.dataParser = handler.getRequestParser();
    }

    /**
     * Обрабатывает сообщение, пришедшее от пользователя
     * @param ctx Контекст соединения с клиентом
     * @param message Инстанс пришедшего сообщения
     * @return Сообщение для отправки клиенту
     * @throws InvalidProtocolBufferException Генерируется если пришедшее сообщение не удалось расковырять парсером,
     * установленном в свойстве dataParser
     */
    public Response.ServerMessage.Builder execute(ChannelHandlerContext ctx, Request.ClientMessage message)
            throws InvalidProtocolBufferException
    {
        // парсим пришедший запрос
        TRequestType request = dataParser.parseFrom(message.getData());

        // создаём ответ, который потом пошлём клиенту
        Response.ServerMessage.Builder response = Response.ServerMessage.newBuilder();
        response.setSuccess(false);

        // если требуется наличие токена, проверяем, авторизировано но соединение
        if (handler.isTokenRequired()) {
            if (!handler.validateToken(ctx)) {
                // если соединение не авторизирвоано, возвращаем пустой ответ с полем success равным false
                return response;
            }
        }
        // передаём управление обработчику запросов
        GeneratedMessageV3 res = handler.execute(ctx, request, response);
        if (res == null) {
            // если ответ пуст, то и возвращаем пустой отвед дальше
            response = null;
        } else {
            // если ответ не пуст, устанавливаем нужный тип и значение ответа
            response.setType(handler.getResponseType()).setData(res.toByteString());
        }
        return response;
    }
}

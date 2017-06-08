package wg_test.chat.server.requestHandler;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.proto.MessageRequest;
import wg_test.chat.proto.Response;
import wg_test.chat.server.RequestHandler;
import wg_test.chat.server.ServiceLocator;
import wg_test.chat.server.entity.OnlineUser;
import wg_test.chat.server.entity.UserToken;
import wg_test.chat.server.message.PrivateMessage;
import wg_test.chat.server.message.PublicMessage;
import wg_test.chat.server.message.ResponseBuilder;
import wg_test.chat.server.service.MessageService;

/**
 * Обработчик запросов на удаление сообщений
 */
public class MessageDeleteRequestHandler extends RequestHandler<MessageRequest.DeleteMessage>
{
    /**
     * Сервис работы с сообщениями в чате
     */
    private MessageService service;

    /**
     * Билдер сообщений, отправляемых клиентам
     */
    private ResponseBuilder responseBuilder;

    public MessageDeleteRequestHandler(ServiceLocator locator)
    {
        super(locator);
        service = locator.getMessageService();
        responseBuilder = new ResponseBuilder();
    }

    /**
     * Для обработчика требуется проверка токена
     * @return True
     */
    @Override
    public boolean isTokenRequired()
    {
        return true;
    }

    @Override
    public Response.ServerMessage.MessageType getResponseType()
    {
        return Response.ServerMessage.MessageType.DELETE_MESSAGE;
    }

    @Override
    public Parser<MessageRequest.DeleteMessage> getRequestParser()
    {
        return MessageRequest.DeleteMessage.parser();
    }

    /**
     * Обрабатывает запрос на удаление определённого сообщения в чате
     * @param ctx Контекст соединения
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    @Override
    public GeneratedMessageV3 execute(
            ChannelHandlerContext ctx,
            MessageRequest.DeleteMessage request,
            Response.ServerMessage.Builder response
    ) {

        GeneratedMessageV3 result;
        UserToken token = getServiceLocator().getTokenContextMap().getTokenByContext(ctx);
        int chatId = request.getChatId();
        if (chatId <= 0) {
            result = deletePublicMessage(token.getUser(), request, response);
        } else {
            result = deletePrivateMessage(token.getUser(), request, response);
        }
        return result;
    }

    /**
     * Обрабатывает запрос на удаление публичного сообщения
     * @param user Инстанс текущего пользователя
     * @param request Запрос, пришедший с клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    private GeneratedMessageV3 deletePublicMessage(
            OnlineUser user,
            MessageRequest.DeleteMessage request,
            Response.ServerMessage.Builder response
    ) {
        // получаем удаляемое сообщение
        PublicMessage message = service.getPublicMessageById(request.getMessageId());
        if (message == null) {
            // если сообщение не найдено - говорим об этом
            return handleErrorRequest(response, 1, "Message with id#" + request.getMessageId() + " not exists");
        }
        if (message.getFromId() != user.getId()) {
            // если сообщение создано другим пользователем
            return handleErrorRequest(response, 1, "Message with id#" + request.getMessageId() + " has another owner");
        }
        if (!service.deletePublicMessage(message)) {
            // если не удалось удалить сообщение
            return handleErrorRequest(response, 1, "Failed to delete public message with id#" + message.getId());
        }
        // рассылаем сообщение о удалении всем активным пользователям
        getServiceLocator().getMessageQueueService().enqueueMessage(
                responseBuilder.buildPublicMessageDeleteBroadcastMessage(message)
        );
        response.setSuccess(true);
        return null;
    }

    /**
     * Обрабатывает запрос на удаление приватного сообщения
     * @param user Инстанс текущего пользователя
     * @param request Запрос, пришедший с клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    private GeneratedMessageV3 deletePrivateMessage(
            OnlineUser user,
            MessageRequest.DeleteMessage request,
            Response.ServerMessage.Builder response
    ) {
        // получаем удаляемое сообщение
        PrivateMessage message = service.getPrivateMessageById(request.getMessageId());
        if (message == null) {
            // если сообщение не найдено - говорим об этом
            return handleErrorRequest(response, 1, "Message with id#" + request.getMessageId() + " not exists");
        }
        if (message.getFromId() != user.getId()) {
            // если сообщение создано другим пользователем
            return handleErrorRequest(response, 1, "Message with id#" + request.getMessageId() + " has another owner");
        }
        if (!service.deletePrivateMessage(message)) {
            // если не удалось удалить сообщение
            return handleErrorRequest(response, 1, "Failed to delete private message with id#" + message.getId());
        }
        // сообщаем участникам чатика о удалении сообщения
        getServiceLocator().getMessageQueueService().enqueueMessage(
                responseBuilder.buildPrivateMessageDeleteBroadcastMessage(message),
                message.getToId(),
                message.getFromId()
        );
        response.setSuccess(true);
        return null;
    }
}

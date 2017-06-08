package wg_test.chat.server.requestHandler;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.proto.MessageRequest;
import wg_test.chat.proto.MessageResponse;
import wg_test.chat.proto.Response;
import wg_test.chat.server.message.PrivateMessage;
import wg_test.chat.server.service.MessageService;
import wg_test.chat.server.RequestHandler;
import wg_test.chat.server.ServiceLocator;
import wg_test.chat.server.entity.OnlineUser;
import wg_test.chat.server.entity.UserToken;
import wg_test.chat.server.message.PublicMessage;
import wg_test.chat.server.message.ResponseBuilder;

public class MessageUpdateRequestHandler extends RequestHandler<MessageRequest.UpdateMessage>
{
    private MessageService service;
    private ResponseBuilder responseBuilder;

    public MessageUpdateRequestHandler(ServiceLocator locator)
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
        return Response.ServerMessage.MessageType.UPDATE_MESSAGE;
    }

    @Override
    public Parser<MessageRequest.UpdateMessage> getRequestParser()
    {
        return MessageRequest.UpdateMessage.parser();
    }

    /**
     * Обрабатвает запрос на изменение уже существующего сообщения
     * @param ctx Контекст соединения
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    @Override
    public GeneratedMessageV3 execute(
            ChannelHandlerContext ctx,
            MessageRequest.UpdateMessage request,
            Response.ServerMessage.Builder response
    ) {

        GeneratedMessageV3 result;
        UserToken token = getServiceLocator().getTokenContextMap().getTokenByContext(ctx);
        int chatId = request.getChatId();
        if (chatId <= 0) {
            result = updatePublicMessage(token.getUser(), request, response);
        } else {
            result = updatePrivateMessage(token.getUser(), request, response);
        }
        return result;
    }

    /**
     * Обрабатвает запрос на изменение уже существующего публичного сообщения
     * @param user Инстанс текущего пользователя
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    private GeneratedMessageV3 updatePublicMessage(
            OnlineUser user,
            MessageRequest.UpdateMessage request,
            Response.ServerMessage.Builder response
    ) {
        // проверяем существует ли сообщение
        PublicMessage message = service.getPublicMessageById(request.getMessageId());
        if (message == null) {
            return handleErrorRequest(response, 1, "Message with id#" + request.getMessageId() + " not exists");
        }
        // и создано ли сообщение текущим пользователем
        if (message.getFromId() != user.getId()) {
            return handleErrorRequest(response, 1, "Message with id#" + request.getMessageId() + " has another owner");
        }
        // и изменился ли текст сообщения
        if (message.getMessage().equals(request.getMessage())) {
            return handleErrorRequest(response, 1, "Message with id#" + request.getMessageId() + " has same content");
        }

        // изменяем текст сообщения
        if (!service.updatePublicMessage(message, request.getMessage())) {
            // если что-то пошло не так, сообщаем клиенту
            return handleErrorRequest(response, 1, "Failed to update public message with id#" + request.getMessageId());
        }
        // отправляем всем активным пользователям информацию о изменении сообщения
        getServiceLocator().getMessageQueueService().enqueueMessage(
                responseBuilder.buildPublicMessageUpdateBroadcastMessage(message)
        );
        // генерируем ответ
        response.setSuccess(true);
        MessageResponse.UpdateMessage result = responseBuilder.buildPublicMessageUpdateResponse(message);
        return result;
    }

    /**
     * Обрабатвает запрос на изменение уже существующего приватного сообщения
     * @param user Инстанс текущего пользователя
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    private GeneratedMessageV3 updatePrivateMessage(
            OnlineUser user,
            MessageRequest.UpdateMessage request,
            Response.ServerMessage.Builder response
    ) {
        // получам сообщение, проверяем, что оно вообще существует
        PrivateMessage message = service.getPrivateMessageById(request.getMessageId());
        if (message == null) {
            return handleErrorRequest(response, 1, "Message with id#" + request.getMessageId() + " not exists");
        }
        // проверяем, что сообщение создано текущим пользователем
        if (message.getFromId() != user.getId()) {
            return handleErrorRequest(response, 1, "Message with id#" + request.getMessageId() + " has another owner");
        }
        // проверяем, что текст сообщения изменился
        if (message.getMessage().equals(request.getMessage())) {
            return handleErrorRequest(response, 1, "Message with id#" + request.getMessageId() + " has same content");
        }
        // пробуем сохранисть сообщение
        if (!service.updatePrivateMessage(message, request.getMessage())) {
            // если что-то пошло не так, сообщаем об этом
            return handleErrorRequest(response, 1, "Failed to update private message with id#" + request.getMessageId());
        }
        // отправляем участникам сообщения информацию о том, что сообщение изменилось
        getServiceLocator().getMessageQueueService().enqueueMessage(
                responseBuilder.buildPrivateMessageUpdateBroadcastMessage(message),
                message.getToId(),
                message.getFromId()
        );
        // генерируем ответ для отправки клиенту
        MessageResponse.UpdateMessage result = responseBuilder.buildPrivateMessageUpdateResponse(message);
        response.setSuccess(true);
        return result;
    }
}

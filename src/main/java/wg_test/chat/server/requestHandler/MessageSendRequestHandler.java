package wg_test.chat.server.requestHandler;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.proto.MessageRequest;
import wg_test.chat.proto.MessageResponse;
import wg_test.chat.proto.Response;
import wg_test.chat.server.*;
import wg_test.chat.server.entity.OnlineUser;
import wg_test.chat.server.entity.User;
import wg_test.chat.server.entity.UserToken;
import wg_test.chat.server.message.PrivateMessage;
import wg_test.chat.server.message.PublicMessage;
import wg_test.chat.server.message.ResponseBuilder;
import wg_test.chat.server.service.MessageService;
import wg_test.chat.server.service.UserService;

/**
 * Обработчик запросов на создание нового сообщения
 */
public class MessageSendRequestHandler extends RequestHandler<MessageRequest.SendMessage>
{
    /**
     * Билдер
     */
    private ResponseBuilder responseBuilder;

    public MessageSendRequestHandler(ServiceLocator locator)
    {
        super(locator);
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
        return Response.ServerMessage.MessageType.SEND_MESSAGE;
    }

    @Override
    public Parser<MessageRequest.SendMessage> getRequestParser()
    {
        return MessageRequest.SendMessage.parser();
    }

    /**
     * Обрабатывает запросы на создание нового сообщения
     * @param ctx Контекст соединения
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    @Override
    public GeneratedMessageV3 execute(
            ChannelHandlerContext ctx,
            MessageRequest.SendMessage request,
            Response.ServerMessage.Builder response
    ) {

        UserToken token = getServiceLocator().getTokenContextMap().getTokenByContext(ctx);
        int recipientId = request.getToId();
        if (recipientId == 0) {
            return sendPublic(token.getUser(), request, response);
        } else {
            return sendPrivate(token.getUser(), request, response);
        }
    }

    /**
     * Обрабатывает запрос на создание нового публичного сообщения
     * @param user Инстанс пользователя, создающего сообщение
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    private GeneratedMessageV3 sendPublic(
            OnlineUser user,
            MessageRequest.SendMessage request,
            Response.ServerMessage.Builder response
    ) {
        MessageService s = getServiceLocator().getMessageService();
        // пробуем создать сообщения
        PublicMessage message = s.createPublicMessage(user, request.getMessage());
        if (message == null) {
            // если сообщение создать не удалось, сообщаем об этом
            return handleErrorRequest(response, 1, "Failed to create public message");
        }
        // сообщаем всем о создании нового сообщения
        getServiceLocator().getMessageQueueService().enqueueMessage(
            responseBuilder.buildPublicMessageBroadcastMessage(message)
        );
        // генерируем ответ текущему клиенту
        MessageResponse.SendMessage result = responseBuilder.buildPublicMessageResponse(message);
        response.setSuccess(true);
        return result;
    }

    /**
     * Обрабатывает запрос на создание нового приватного сообщения
     * @param user Инстанс пользователя, создающего сообщение
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    private GeneratedMessageV3 sendPrivate(
            OnlineUser user,
            MessageRequest.SendMessage request,
            Response.ServerMessage.Builder response
    ) {
        UserService userService = getServiceLocator().getUserService();
        MessageService s = getServiceLocator().getMessageService();

        // проверяем, что получатель сообщения существует
        User recipient = userService.getUserById(request.getToId());
        if (user == null) {
            return handleErrorRequest(response, 2, "Recipient user#" + request.getToId() + " not exists");
        }
        // пробуем создать новое сообщение
        PrivateMessage message = s.createPrivateMessage(user, recipient, request.getMessage());
        if (message == null) {
            // если сообщение создать не удалось, сообщаем об этом
            return handleErrorRequest(response, 1, "Failed to create private message");
        }
        // сообщаем получателю и отправителю о создании сообщения
        getServiceLocator().getMessageQueueService().enqueueMessage(
            responseBuilder.buildPrivateMessageBroadcastMessage(message), user.getId(), recipient.getId()
        );
        // генерируем ответ текущему пользователю
        MessageResponse.SendMessage result = responseBuilder.buildPrivateMessageResponse(message);
        response.setSuccess(true);
        return result;
    }
}
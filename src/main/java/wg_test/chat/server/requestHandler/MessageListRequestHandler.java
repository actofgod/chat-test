package wg_test.chat.server.requestHandler;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.entity.User;
import wg_test.chat.proto.MessageRequest;
import wg_test.chat.proto.MessageResponse;
import wg_test.chat.proto.Response;
import wg_test.chat.server.service.MessageService;
import wg_test.chat.server.entity.UserToken;
import wg_test.chat.server.message.PrivateMessage;
import wg_test.chat.server.message.PublicMessage;
import wg_test.chat.server.RequestHandler;
import wg_test.chat.server.ServiceLocator;

/**
 * Обработчик запросов на получение списка сообщений
 */
public class MessageListRequestHandler extends RequestHandler<MessageRequest.ListMessage>
{
    /**
     * Сервис работы с сообщенийми
     */
    private MessageService service;

    public MessageListRequestHandler(ServiceLocator locator)
    {
        super(locator);
        service = locator.getMessageService();
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
        return Response.ServerMessage.MessageType.LIST_MESSAGES;
    }

    @Override
    public Parser<MessageRequest.ListMessage> getRequestParser()
    {
        return MessageRequest.ListMessage.parser();
    }

    /**
     * Обрабатывает запросы получение списка сообщений
     * @param ctx Контекст соединения
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    @Override
    public GeneratedMessageV3 execute(
            ChannelHandlerContext ctx,
            MessageRequest.ListMessage request,
            Response.ServerMessage.Builder response
    ) {

        GeneratedMessageV3 result;
        int chatId = request.getChatId();

        if (chatId == 0) {
            // если айди чата равно нулю, то загружаем публичные сообщения
            result = loadPublicMessages(request, response);
        } else {
            // получаем инстанс текущего пользователя
            UserToken token = getServiceLocator().getTokenContextMap().getTokenByContext(ctx);
            if (token == null) {
                return handleErrorRequest(response, 4, "Current user not exists");
            }
            // получаем инстанс пользователя, информацию о чате с которым загружаем
            User user = getServiceLocator().getUserService().getUserById(chatId);
            if (user == null) {
                // если второй пользователь не найден - ошибка
                return handleErrorRequest(response, 4, "User not exists");
            }
            // загружаем список приватных сообщений
            result = loadPrivateMessages(token.getUser(), user, request, response);
        }
        return result;
    }

    /**
     * Получает список публичных сообщений и формирует ответ для отправки на клиент
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    private MessageResponse.ListMessage loadPublicMessages(
            MessageRequest.ListMessage request,
            Response.ServerMessage.Builder response
    ) {

        long horizon = request.getHorizon();
        MessageResponse.ListMessage.Builder result = MessageResponse.ListMessage.newBuilder();

        if (horizon == 0) {
            horizon = System.currentTimeMillis();
        }
        Iterable<PublicMessage> messages = service.getPublicMessages(horizon, 20);
        for (PublicMessage m: messages) {
            result.addMessages(
                MessageResponse.Message.newBuilder()
                    .setMessageId(m.getId())
                    .setFromId(m.getFromId())
                    .setToId(0)
                    .setTimeSend(m.getDateCreate().getTime())
                    .setMessage(m.getMessage())
                    .build()
            );
        }
        response.setSuccess(true);
        result.setChatId(0);
        return result.build();
    }

    /**
     * Получает список публичных сообщений и формирует ответ для отправки на клиент
     * @param currentUser Инстанс текущего клиента
     * @param user Инстанс второго клиента
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    private MessageResponse.ListMessage loadPrivateMessages(
            User currentUser,
            User user,
            MessageRequest.ListMessage request,
            Response.ServerMessage.Builder response
    ) {
        long horizon = request.getHorizon();
        MessageResponse.ListMessage.Builder result = MessageResponse.ListMessage.newBuilder();

        if (horizon == 0) {
            horizon = System.currentTimeMillis();
        }

        Iterable<PrivateMessage> messages = service.getPrivateMessages(currentUser, user, horizon, 20);
        for (PrivateMessage m: messages) {
            result.addMessages(
                    MessageResponse.Message.newBuilder()
                            .setMessageId(m.getId())
                            .setFromId(m.getFromId())
                            .setToId(m.getToId())
                            .setTimeSend(m.getDateCreate().getTime())
                            .setMessage(m.getMessage())
                            .build()
            );
        }
        response.setSuccess(true);
        result.setChatId(user.getId());
        return result.build();
    }
}

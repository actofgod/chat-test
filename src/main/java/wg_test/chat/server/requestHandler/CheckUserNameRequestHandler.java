package wg_test.chat.server.requestHandler;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.entity.User;
import wg_test.chat.proto.AuthRequest;
import wg_test.chat.proto.AuthResponse;
import wg_test.chat.proto.Response;
import wg_test.chat.server.RequestHandler;
import wg_test.chat.server.ServiceLocator;
import wg_test.chat.server.service.UserService;

/**
 * Класс обработчика запросов на проверку имени создаваемого пользователя
 */
public class CheckUserNameRequestHandler extends RequestHandler<AuthRequest.CheckUserNameMessage>
{
    /**
     * Сервис работы с пользователями
     */
    private UserService userService;

    public CheckUserNameRequestHandler(ServiceLocator locator)
    {
        super(locator);
    }

    @Override
    public Response.ServerMessage.MessageType getResponseType()
    {
        return Response.ServerMessage.MessageType.CHECK_USER_NAME;
    }

    @Override
    public Parser<AuthRequest.CheckUserNameMessage> getRequestParser()
    {
        return AuthRequest.CheckUserNameMessage.parser();
    }

    /**
     * Обрабатывает запрос на проверку имени создаваемого пользователя
     * @param ctx Контекст соединения
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    @Override
    public GeneratedMessageV3 execute(
            ChannelHandlerContext ctx,
            AuthRequest.CheckUserNameMessage request,
            Response.ServerMessage.Builder response
    ) {
        AuthResponse.CheckUserNameMessage.Builder responseBuilder = AuthResponse.CheckUserNameMessage.newBuilder();
        String userName = request.getUsername();

        // проверяем валидность имени пользователя
        if (userName == null || userName.isEmpty()) {
            responseBuilder.setValid(false);
            responseBuilder.setMessage("User name is empty");
        } else if (userName.length() < 3) {
            responseBuilder.setValid(false);
            responseBuilder.setMessage("User name to short");
        } else if (userName.length() >= 64) {
            responseBuilder.setValid(false);
            responseBuilder.setMessage("User name to long");
        } else {
            // вытаскиваем пользоваетля по указанному имени
            User user = getUserService().getUserByName(userName);
            if (user != null) {
                // если пользователь уже существует - сообщаем об этом
                responseBuilder.setValid(false);
                responseBuilder.setMessage("User already exists");
            } else {
                // если такого пользователя ещё нет, то все ок
                responseBuilder.setValid(true);
            }
        }
        return responseBuilder.build();
    }

    private UserService getUserService()
    {
        if (userService == null) {
            userService = getServiceLocator().getUserService();
        }
        return userService;
    }
}

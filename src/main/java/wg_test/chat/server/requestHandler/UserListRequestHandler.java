package wg_test.chat.server.requestHandler;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelHandlerContext;
import wg_test.chat.proto.Response;
import wg_test.chat.proto.UserRequest;
import wg_test.chat.proto.UserResponse;
import wg_test.chat.server.RequestHandler;
import wg_test.chat.server.ServiceLocator;
import wg_test.chat.server.service.UserService;
import wg_test.chat.server.entity.User;

/**
 * Класс обработчика запроса клиента на получение списка пользователей
 */
public class UserListRequestHandler extends RequestHandler<UserRequest.ListMessage>
{
    /**
     * Сервис работы с пользователями
     */
    private UserService userService;

    public UserListRequestHandler(ServiceLocator locator)
    {
        super(locator);
        userService = locator.getUserService();
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
        return Response.ServerMessage.MessageType.USER_LIST;
    }

    @Override
    public Parser<UserRequest.ListMessage> getRequestParser()
    {
        return UserRequest.ListMessage.parser();
    }

    /**
     * Обрабатвает запрос на получение списка всех пользователей
     * @param ctx Контекст соединения
     * @param request Запрос, пришедший от клиента
     * @param response Ответ, отправляемый пользователю
     * @return Сообщение вставляемое в ответ пользователю
     */
    @Override
    public GeneratedMessageV3 execute(
            ChannelHandlerContext ctx,
            UserRequest.ListMessage request,
            Response.ServerMessage.Builder response
    ) {
        UserResponse.ListMessage.Builder result = UserResponse.ListMessage.newBuilder();
        UserResponse.User.Builder userBuilder = UserResponse.User.newBuilder();
        for (User user : userService.getAllUsers()) {
            result.addUsers(
                userBuilder
                    .setId(user.getId())
                    .setUsername(user.getUserName())
                    .setOnline(user.isOnline())
                    .build()
            );
        }
        response.setSuccess(true);
        return result.build();
    }
}

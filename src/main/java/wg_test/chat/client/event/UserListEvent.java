package wg_test.chat.client.event;

import com.google.protobuf.InvalidProtocolBufferException;
import wg_test.chat.client.entity.UserImpl;
import wg_test.chat.proto.Response;
import wg_test.chat.proto.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class UserListEvent extends Event
{
    private UserResponse.ListMessage data;
    private ArrayList<UserImpl> users;

    public UserListEvent(Response.ServerMessage message)
    {
        super(message);
        try {
            if (message.getSuccess()) {
                data = UserResponse.ListMessage.parseFrom(message.getData());
            }
        } catch (InvalidProtocolBufferException e) {
            setErrorInfo(-1, e.getMessage());
        }
    }

    public List<UserImpl> getUserList()
    {
        if (users == null) {
            int count = data.getUsersCount();
            users = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                users.add(new UserImpl(data.getUsers(i)));
            }
        }
        return users;
    }
}

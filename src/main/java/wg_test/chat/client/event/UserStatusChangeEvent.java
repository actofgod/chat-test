package wg_test.chat.client.event;

import com.google.protobuf.InvalidProtocolBufferException;
import wg_test.chat.proto.Response;
import wg_test.chat.proto.UserResponse;

public class UserStatusChangeEvent extends Event
{
    private UserResponse.UserStatusChangeMessage data;

    public UserStatusChangeEvent(Response.ServerMessage message)
    {
        super(message);
        try {
            if (message.getSuccess()) {
                data = UserResponse.UserStatusChangeMessage.parseFrom(message.getData());
            }
        } catch (InvalidProtocolBufferException e) {
            setErrorInfo(-1, e.getMessage());
        }
    }

    public int getUserId()
    {
        return data.getUserId();
    }

    public boolean getOnline()
    {
        return data.getOnline();
    }
}

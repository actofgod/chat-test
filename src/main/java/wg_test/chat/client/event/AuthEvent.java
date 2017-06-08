package wg_test.chat.client.event;

import com.google.protobuf.InvalidProtocolBufferException;
import wg_test.chat.proto.Response;
import wg_test.chat.proto.AuthResponse;

import java.util.Date;

public class AuthEvent extends Event
{
    private AuthResponse.AuthMessage data;
    private Date validBefore;

    public AuthEvent(Response.ServerMessage message)
    {
        super(message);
        try {
            if (message.getSuccess()) {
                data = AuthResponse.AuthMessage.parseFrom(message.getData());
            }
        } catch (InvalidProtocolBufferException e) {
            setErrorInfo(-1, e.getMessage());
        }
    }

    public String getTokenValue()
    {
        return data == null || !isSuccess() ? null : data.getToken();
    }

    public int getUserId()
    {
        return data == null || !isSuccess() ? 0 : data.getUserId();
    }

    public Date getValidBefore()
    {
        if (validBefore == null && data != null && isSuccess()) {
            validBefore = new Date(data.getValidBefore());
        }
        return validBefore;
    }
}

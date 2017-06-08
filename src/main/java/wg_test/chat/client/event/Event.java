package wg_test.chat.client.event;

import com.google.protobuf.InvalidProtocolBufferException;
import wg_test.chat.proto.ErrorInfo;
import wg_test.chat.proto.Response;

public class Event
{
    private ErrorInfo.Error errorInfo;
    private boolean success;

    public Event(Response.ServerMessage message)
    {
        this.success = false;
        try {
            if (message.getSuccess()) {
                success = true;
            } else {
                errorInfo = ErrorInfo.Error.parseFrom(message.getData());
            }
        } catch (InvalidProtocolBufferException e) {
            errorInfo = ErrorInfo.Error.newBuilder().setCode(-1).setReason(e.getMessage()).build();
        }
    }

    public boolean isSuccess()
    {
        return success;
    }

    public int getErrorCode()
    {
        return success ? 0 : errorInfo.getCode();
    }

    public String getErrorMessage()
    {
        return success ? null : errorInfo.getReason();
    }

    final protected void setErrorInfo(int code, final String message)
    {
        if (errorInfo == null) {
            errorInfo = ErrorInfo.Error.newBuilder().setCode(code).setReason(message).build();
        }
    }
}

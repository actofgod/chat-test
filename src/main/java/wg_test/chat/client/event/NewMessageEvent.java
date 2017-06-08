package wg_test.chat.client.event;

import com.google.protobuf.InvalidProtocolBufferException;
import wg_test.chat.client.entity.UserImpl;
import wg_test.chat.client.entity.UserMessageImpl;
import wg_test.chat.proto.MessageResponse;
import wg_test.chat.proto.Response;

import java.util.HashMap;

public class NewMessageEvent extends Event
{
    private MessageResponse.Message data;
    private UserMessageImpl message;
    private HashMap<Integer, UserImpl> userList;
    private int currentUserId;

    public NewMessageEvent(Response.ServerMessage message)
    {
        super(message);
        try {
            if (message.getSuccess()) {
                data = MessageResponse.Message.parseFrom(message.getData());
                System.out.println(data);
            }
        } catch (InvalidProtocolBufferException e) {
            setErrorInfo(-1, e.getMessage());
        }
    }

    public NewMessageEvent setUserMap(HashMap<Integer, UserImpl> value)
    {
        userList = value;
        return this;
    }

    public NewMessageEvent setCurrentUserId(int userId)
    {
        currentUserId = userId;
        return this;
    }

    public UserImpl getFromUser()
    {
        return userList.get(data.getFromId());
    }

    public UserImpl getToUser()
    {
        return userList.get(data.getToId());
    }

    public UserMessageImpl getMessage()
    {
        if (message == null) {
            if (data.getToId() == 0) {
                message = new UserMessageImpl(userList.get(0), getFromUser(), data);
            } else {
                if (currentUserId == data.getToId()) {
                    message = new UserMessageImpl(getFromUser(), getToUser(), data);
                } else {
                    message = new UserMessageImpl(getToUser(), getFromUser(), data);
                }
            }
        }
        return message;
    }
}

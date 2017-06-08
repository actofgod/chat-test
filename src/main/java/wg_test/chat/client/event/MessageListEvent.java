package wg_test.chat.client.event;

import com.google.protobuf.InvalidProtocolBufferException;
import wg_test.chat.client.entity.UserImpl;
import wg_test.chat.client.entity.UserMessageImpl;
import wg_test.chat.proto.MessageResponse;
import wg_test.chat.proto.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageListEvent extends Event
{
    private MessageResponse.ListMessage data;
    private ArrayList<UserMessageImpl> messages;
    private HashMap<Integer, UserImpl> userList;

    public MessageListEvent(Response.ServerMessage message)
    {
        super(message);
        try {
            if (message.getSuccess()) {
                data = MessageResponse.ListMessage.parseFrom(message.getData());
                System.out.println(data);
            }
        } catch (InvalidProtocolBufferException e) {
            setErrorInfo(-1, e.getMessage());
        }
    }

    public void setUserMap(HashMap<Integer, UserImpl> value)
    {
        userList = value;
    }

    public UserImpl getChatUser()
    {
        return userList.get(data.getChatId());
    }

    public List<UserMessageImpl> getMessageList()
    {
        if (messages == null) {
            int count = data.getMessagesCount();
            UserImpl from, to;
            messages = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                from = userList.get(data.getMessages(i).getFromId());
                to = userList.get(data.getMessages(i).getToId());
                if (from.getId() == data.getChatId()) {
                    messages.add(new UserMessageImpl(from, data.getMessages(i)));
                } else {
                    messages.add(new UserMessageImpl(to, from, data.getMessages(i)));
                }
            }
        }
        return messages;
    }
}

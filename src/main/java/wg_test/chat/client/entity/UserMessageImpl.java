package wg_test.chat.client.entity;

import wg_test.chat.proto.MessageResponse;

import java.util.Date;

public class UserMessageImpl
{
    private UserImpl anotherUser;
    private UserImpl chat;
    private int id;
    private String message;
    private Date dateSend;
    private Date dateView;
    private Date dateEdit;

    public UserMessageImpl(UserImpl chatUser, MessageResponse.Message mess)
    {
        this(chatUser, chatUser, mess);
    }

    public UserMessageImpl(UserImpl chatUser, UserImpl another, MessageResponse.Message mess)
    {
        this.anotherUser = another;
        this.chat = chatUser;
        this.id = mess.getMessageId();
        this.message = mess.getMessage();
        this.dateSend = new Date(mess.getTimeSend());
        if (mess.getDateView() <= 0) {
            if (this.chat.getId() == 0) {
                this.dateView = this.dateSend;
            } else {
                this.dateView = null;
            }
        }
        if (mess.getLastUpdate() <= 0) {
            this.dateEdit = null;
        }
    }

    public UserImpl getChat()
    {
        return chat;
    }

    public UserImpl getAnotherUser()
    {
        return anotherUser;
    }

    public int getId()
    {
        return id;
    }

    public String getMessage()
    {
        return message;
    }

    public Date getDateSend()
    {
        return dateSend;
    }
}

package wg_test.chat.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import wg_test.chat.client.event.*;
import wg_test.chat.entity.User;
import wg_test.chat.proto.*;

public class NetworkManager extends SimpleChannelInboundHandler<Response.ServerMessage>
{
    private Channel channel;

    private EventDispatcher eventDispatcher;

    public NetworkManager()
    {
        super();
        eventDispatcher = new EventDispatcher();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx)
    {
        channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response.ServerMessage msg) throws Exception
    {
        System.out.println("Received message: " + msg);

        Response.ServerMessage.MessageType messageType = msg.getType();

        Event event = null;
        if (messageType.getNumber() == Response.ServerMessage.MessageType.AUTH_VALUE) {
            event = new AuthEvent(msg);
        } else if (messageType.getNumber() == Response.ServerMessage.MessageType.REGISTER_VALUE) {
            event = new RegisterEvent(msg);
        } else if (messageType.getNumber() == Response.ServerMessage.MessageType.USER_LIST_VALUE) {
            event = new UserListEvent(msg);
        } else if (messageType.getNumber() == Response.ServerMessage.MessageType.USER_STATUS_CHANGE_VALUE) {
            event = new UserStatusChangeEvent(msg);
        } else if (messageType.getNumber() == Response.ServerMessage.MessageType.LIST_MESSAGES_VALUE) {
            event = new MessageListEvent(msg);
        } else if (messageType.getNumber() == Response.ServerMessage.MessageType.NEW_MESSAGE_VALUE) {
            event = new NewMessageEvent(msg);
        } else {
            System.out.println("Message with type " + messageType.toString() + " ignored");
        }
        if (event != null) {
            eventDispatcher.dispatchEvent(event);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        ctx.close();
    }

    public void onAuth(EventListener<AuthEvent> listener)
    {
        eventDispatcher.addEventListener(AuthEvent.class, listener);
    }

    public void onRegister(EventListener<RegisterEvent> listener)
    {
        eventDispatcher.addEventListener(RegisterEvent.class, listener);
    }

    public void onUserList(EventListener<UserListEvent> listener)
    {
        eventDispatcher.addEventListener(UserListEvent.class, listener);
    }

    public void onUserStatusChange(EventListener<UserStatusChangeEvent> listener)
    {
        eventDispatcher.addEventListener(UserStatusChangeEvent.class, listener);
    }

    public void onMessageList(EventListener<MessageListEvent> listener)
    {
        eventDispatcher.addEventListener(MessageListEvent.class, listener);
    }

    public void onNewMessage(EventListener<NewMessageEvent> listener)
    {
        eventDispatcher.addEventListener(NewMessageEvent.class, listener);
    }

    public void sendAuthRequest(String username, String password)
    {
        Request.ClientMessage req = Request.ClientMessage.newBuilder()
                .setType(Request.ClientMessage.MessageType.AUTH)
                .setData(
                        AuthRequest.AuthMessage.newBuilder()
                                .setUsername(username)
                                .setPassword(password)
                                .build().toByteString()
                )
                .build();
        channel.writeAndFlush(req);
    }

    public void sendRegisterRequest(String username, String password)
    {
        Request.ClientMessage req = Request.ClientMessage.newBuilder()
                .setType(Request.ClientMessage.MessageType.REGISTER)
                .setData(
                        AuthRequest.RegisterMessage.newBuilder()
                                .setUsername(username)
                                .setPassword(password)
                                .build().toByteString()
                )
                .build();
        channel.writeAndFlush(req);
    }

    public void sendGetUserListRequest()
    {
        Request.ClientMessage req = Request.ClientMessage.newBuilder()
                .setType(Request.ClientMessage.MessageType.USER_LIST)
                .setData(
                        UserRequest.ListMessage.newBuilder()
                                .build().toByteString()
                )
                .build();
        System.out.println("Send message: " + req);
        channel.writeAndFlush(req);
    }

    public void sendGetMessages(User user, long horizon)
    {
        Request.ClientMessage req = Request.ClientMessage.newBuilder()
                .setType(Request.ClientMessage.MessageType.LIST_MESSAGES)
                .setData(
                        MessageRequest.ListMessage.newBuilder()
                                .setChatId(user.getId())
                                .setHorizon(horizon)
                                .build().toByteString()
                )
                .build();
        System.out.println("Send message: " + req);
        channel.writeAndFlush(req);
    }

    public void sendNewMessageRequest(User user, String text)
    {
        Request.ClientMessage req = Request.ClientMessage.newBuilder()
                .setType(Request.ClientMessage.MessageType.SEND_MESSAGE)
                .setData(
                        MessageRequest.SendMessage.newBuilder()
                                .setToId(user.getId())
                                .setMessage(text)
                                .build().toByteString()
                )
                .build();
        System.out.println("Send message: " + req);
        channel.writeAndFlush(req);
    }

    public EventDispatcher getEventDispatcher()
    {
        return eventDispatcher;
    }
}

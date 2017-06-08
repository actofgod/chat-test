package wg_test.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import wg_test.chat.client.entity.UserImpl;
import wg_test.chat.client.panel.ChatPanel;
import wg_test.chat.client.panel.LoginPanel;
import wg_test.chat.client.panel.RegisterPanel;
import wg_test.chat.client.panel.chat.ChatView;

public class Client extends Application
{
    final private static String HOST = "172.17.0.1";
    final private static short PORT = 11235;

    private Stage primaryStage;

    private NetworkManager network;
    private int userId;
    private String token;

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private ChatPanel chatPanel;

    static private EventLoopGroup group;

    public Client()
    {
        loginPanel = new LoginPanel();
        registerPanel = new RegisterPanel();
        chatPanel = new ChatPanel();
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;

        connect();

        loginPanel.getLoginButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                loginPanel.getLoginButton().setDisable(true);
                network.sendAuthRequest(loginPanel.getUserName(), loginPanel.getPassword());
            }
        });
        loginPanel.getRegisterButton().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                loginPanel.hide();
                registerPanel.drawPanel(primaryStage);
            }
        });
        loginPanel.drawPanel(primaryStage);

        registerPanel.getRegisterButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                registerPanel.getRegisterButton().setDisable(true);
                network.sendRegisterRequest(registerPanel.getUserName(), registerPanel.getPassword());
            }
        });
        registerPanel.getLoginButton().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                registerPanel.hide();
                loginPanel.drawPanel(primaryStage);
            }
        });

        primaryStage.show();

        network.onAuth(event -> {
            if (event.isSuccess()) {
                token = event.getTokenValue();
                userId = event.getUserId();
                loginPanel.hide();
                showChat();
            } else {
                loginPanel.getLoginButton().setDisable(false);
                loginPanel.getResultField().setText(event.getErrorMessage());
            }
        });
        network.onRegister(event -> {
            if (event.isSuccess()) {
                token = event.getTokenValue();
                userId = event.getUserId();
                registerPanel.hide();
                showChat();
            } else {
                registerPanel.getRegisterButton().setDisable(false);
                registerPanel.getResultField().setText(event.getErrorMessage());
            }
        });
    }

    public static void main(String[] args)
    {
        try {
            Client.launch(args);
        } finally {
            if (group != null) {
                group.shutdownGracefully();
                group = null;
            }
        }
    }

    private void connect()
    {
        group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer());
            Channel c = bootstrap.connect(HOST, PORT).sync().channel();
            network = c.pipeline().get(NetworkManager.class);
        } catch (InterruptedException e) {

        }
    }

    private void showChat()
    {
        chatPanel.drawPanel(primaryStage);
        network.onUserList(event -> {
            if (event.isSuccess()) {
                chatPanel.getUserList().setUserList(event.getUserList());
            }
        });
        network.onUserStatusChange(event -> {
            if (event.isSuccess()) {
                chatPanel.getUserList().updateUserStatus(event.getUserId(), event.getOnline());
            }
        });
        network.sendGetUserListRequest();

        network.onMessageList(event -> {
            if (event.isSuccess()) {
                event.setUserMap(chatPanel.getUserList().getAllUsers());
                ChatView chat = chatPanel.getChat(event.getChatUser());
                chat.addMessages(event.getMessageList());
            }
        });

        network.onNewMessage(event -> {
            if (event.isSuccess()) {
                event.setUserMap(chatPanel.getUserList().getAllUsers());
                event.setCurrentUserId(userId);
                ChatView chat = chatPanel.getChat(event.getMessage().getChat());
                chat.addMessage(event.getMessage());
            }
        });

        chatPanel.getUserList().getListView().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                UserImpl user = chatPanel.getUserList().getListView().getSelectionModel().getSelectedItem();
                ChatView chat = chatPanel.setCurrentChat(user);
                if (!chat.isLoaded()) {
                    network.sendGetMessages(user, chat.getHorizon());
                }
                if (!chat.hasSendHandler()) {
                    chat.setSendHandler(ev -> {
                        String text = chat.getTextArea().getText();
                        chat.getTextArea().setText("");
                        network.sendNewMessageRequest(user, text);
                    });
                }
            }
        });
    }
}

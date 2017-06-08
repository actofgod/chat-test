package wg_test.chat.client.panel;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import wg_test.chat.client.entity.UserImpl;
import wg_test.chat.client.panel.chat.ChatView;
import wg_test.chat.client.panel.chat.UserList;

import java.util.HashMap;

public class ChatPanel
{
    private Scene scene;
    private HBox mainLayout;
    private UserList userList;
    private ChatView currentChat;
    private HashMap<UserImpl, ChatView> chats;

    public ChatPanel()
    {
        mainLayout = new HBox();
        userList = new UserList();
        mainLayout.getChildren().add(userList.getListView());

        currentChat = new ChatView(userList.getGeneralUser());
        chats = new HashMap<>();
        chats.put(userList.getGeneralUser(), currentChat);
        mainLayout.getChildren().add(currentChat.getLayout());
    }

    public UserList getUserList()
    {
        return userList;
    }

    public void drawPanel(Stage primaryStage)
    {
        if (scene == null) {
            scene = new Scene(mainLayout, 800, 500);
        }
        primaryStage.setScene(scene);
    }

    public ChatView setCurrentChat(UserImpl user)
    {
        currentChat = chats.get(user);
        if (currentChat == null) {
            currentChat = new ChatView(user);
            chats.put(user, currentChat);
        }
        mainLayout.getChildren().remove(1);
        mainLayout.getChildren().add(currentChat.getLayout());
        return currentChat;
    }

    public ChatView getChat(UserImpl user)
    {
        ChatView chat = chats.get(user);
        if (chat == null) {
            chat = new ChatView(user);
            chats.put(user, chat);
        }
        return chat;
    }
}

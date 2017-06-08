package wg_test.chat.client.panel.chat;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import wg_test.chat.client.entity.UserImpl;
import wg_test.chat.client.entity.UserMessageImpl;

import java.util.LinkedList;
import java.util.List;

public class ChatView
{
    private UserImpl user;
    private VBox layout;
    private TextArea newMessageArea;
    private Text messages;
    private LinkedList<UserMessageImpl> messageList;
    private Button sendButton;
    private boolean isLoaded;
    private EventHandler<ActionEvent> onSend;

    public ChatView(UserImpl user)
    {
        this.user = user;
        this.layout = new VBox();
        this.newMessageArea = new TextArea();

        this.messages = new Text();
        this.messages.setText("No data");
        this.messageList = new LinkedList<>();

        this.sendButton = new Button("Send");
        this.onSend = null;

        this.layout.getChildren().add(this.messages);
        this.layout.getChildren().add(this.newMessageArea);
        this.layout.getChildren().add(this.sendButton);

        this.isLoaded = false;
    }

    public VBox getLayout()
    {
        return layout;
    }

    public boolean isLoaded()
    {
        return isLoaded;
    }

    public long getHorizon()
    {
        return messageList.isEmpty() ? 0 : messageList.peekFirst().getDateSend().getTime();
    }

    public void setSendHandler(EventHandler<ActionEvent> eventHandler)
    {
        onSend = eventHandler;
        sendButton.setOnAction(eventHandler);
    }

    public TextArea getTextArea()
    {
        return newMessageArea;
    }

    public void addMessages(List<UserMessageImpl> messages)
    {
        if (messages.isEmpty()) {
            isLoaded = true;
            return;
        }

        StringBuilder strings = new StringBuilder();

        System.out.println("Messages: " + messages.size());
        for (UserMessageImpl m : messages) {
            messageList.push(m);
        }
        for (UserMessageImpl m : messageList) {
            strings.append("[").append(m.getAnotherUser().getUserName()).append("] at ")
                    .append(m.getDateSend().toString()).append(": ")
                    .append(m.getMessage()).append("\n");
        }
        this.messages.setText(strings.toString());
    }

    public void addMessage(UserMessageImpl message)
    {
        StringBuilder strings = new StringBuilder();
        messageList.add(message);
        strings.append(this.messages.getText());
        strings.append("[").append(message.getAnotherUser().getUserName()).append("] at ")
                .append(message.getDateSend().toString()).append(": ")
                .append(message.getMessage()).append("\n");
        this.messages.setText(strings.toString());
    }

    public boolean hasSendHandler()
    {
        return onSend != null;
    }
}

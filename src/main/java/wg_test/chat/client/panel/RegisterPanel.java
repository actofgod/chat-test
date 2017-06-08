package wg_test.chat.client.panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RegisterPanel
{
    private Scene scene;
    private GridPane grid;
    private TextField userTextField;
    private PasswordField passwordField;
    private Text loginButton;
    private Button registerButton;
    private Text resultField;

    public RegisterPanel()
    {
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Register");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        loginButton = new Text("Sign in");
        loginButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, FontPosture.ITALIC, 11));
        loginButton.setUnderline(true);
        loginButton.setCursor(Cursor.HAND);

        registerButton = new Button("Register");

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(loginButton);
        hbBtn.getChildren().add(registerButton);
        grid.add(hbBtn, 1, 4);

        resultField = new Text();
        grid.add(resultField, 1, 6);
    }

    public Button getRegisterButton()
    {
        return registerButton;
    }

    public Text getResultField()
    {
        return resultField;
    }

    public String getUserName()
    {
        return userTextField.getText();
    }

    public String getPassword()
    {
        return passwordField.getText();
    }

    public Text getLoginButton()
    {
        return loginButton;
    }

    public void drawPanel(Stage primaryStage)
    {
        if (scene == null) {
            scene = new Scene(grid, 350, 300);
        }
        primaryStage.setScene(scene);
    }

    public void hide()
    {
        userTextField.setText("");
        passwordField.setText("");
        resultField.setText("");
    }
}

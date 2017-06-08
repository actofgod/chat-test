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

public class LoginPanel
{
    private Scene scene;
    private GridPane grid;
    private TextField userTextField;
    private PasswordField passwordField;
    private Button loginButton;
    private Text registerButton;
    private Text resultField;

    public LoginPanel()
    {
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Sign in");
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

        registerButton = new Text("Register");
        registerButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, FontPosture.ITALIC, 11));
        registerButton.setUnderline(true);
        registerButton.setCursor(Cursor.HAND);

        loginButton = new Button("Sign in");

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(registerButton);
        hbBtn.getChildren().add(loginButton);
        grid.add(hbBtn, 1, 4);

        resultField = new Text();
        grid.add(resultField, 1, 6);
    }

    public Button getLoginButton()
    {
        return loginButton;
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

    public Text getRegisterButton()
    {
        return registerButton;
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

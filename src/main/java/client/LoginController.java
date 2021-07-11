package client;

import database.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField txUsername;
    @FXML
    private PasswordField txPassword;

    @FXML
    void register(ActionEvent event) throws IOException {
        Parent chat = FXMLLoader.load(getClass().getResource("/Register.fxml"));
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(chat,800,600));
        stage.setResizable(false);
        stage.show();
    }

    public void login(ActionEvent event) throws IOException {


        String username = txUsername.getText();
        String pw = txPassword.getText();
        if(User.checkPassword(username,pw)){
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Chat.fxml"));

            try {
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root,800,600));
                stage.setTitle("CHAT");
                stage.setResizable(false);
                ChatController controller = (ChatController) loader.getController();
                if (controller == null) {
                    System.out.println("hihihihi");
                }
                controller.setUsername(username);
                stage.show();
            } catch (Exception e) {
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Sai tài khoản hoặc mật khẩu", ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();
        }

    }
}

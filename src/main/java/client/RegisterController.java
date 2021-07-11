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

public class RegisterController {
    @FXML
    private TextField txUsername;

    @FXML
    private PasswordField pw2;

    @FXML
    private PasswordField pw1;

    @FXML
    public void register(ActionEvent event) throws IOException {
        String username = txUsername.getText();
        String pw = pw1.getText();
        String pw2Text = pw2.getText();

        if(!pw.equals(pw2Text)){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Sai tài khoản hoặc mật khẩu", ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();
        }
        if(pw.isBlank() || username.isBlank() || pw2Text.isBlank()){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Chưa nhập đủ thông tin", ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();
        }

        else {
            if(User.addStudent(username,pw)){
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/Login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Trùng tên username", ButtonType.OK);
                alert.setHeaderText(null);
                alert.showAndWait();
            }
        }

    }
}

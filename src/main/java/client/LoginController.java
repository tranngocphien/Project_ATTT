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
import mahoa.FileUtil;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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

    public void login(ActionEvent event) throws IOException, NoSuchAlgorithmException {


        String username = txUsername.getText();
        String pw = txPassword.getText();
        if(User.checkPassword(username,pw)){
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Chat.fxml"));

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair kpg = generator.generateKeyPair();

            String private_key = "key/"+username+"/private_key.txt";
            String public_key = "key/"+username+"/public_key.txt";

            File privateKey = new File(private_key);
            File publicKey = new File(public_key);

            File dir = new File("key/"+username);
            if (dir.mkdir()){
                System.out.println("crated dir " +dir.getPath());
            }

            FileUtil.writeToFile(privateKey, Base64.getEncoder().encode(kpg.getPrivate().getEncoded()));
            FileUtil.writeToFile(publicKey, Base64.getEncoder().encode(kpg.getPublic().getEncoded()));

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

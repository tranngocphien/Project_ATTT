package client;

import database.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import mahoa.AESUtil;
import mahoa.FileUtil;
import mahoa.RSAUtil;
import model.Message;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;

public class ChatController {
    private String username;
    private ChatClientEndpoint endpoint;

    @FXML
    private ListView<String> lvChat;
    @FXML
    private TextField txContent;
    @FXML
    private ComboBox<String> cbListUser;
    @FXML
    private Label lbUsername;

    public void setUsername(String username){
        this.username = username;
        lbUsername.setText(this.username);

        List<String> lUsername = User.getAllUsername();
        for(int i = 0; i < lUsername.size(); i++){
            if(lUsername.get(i).equals(username)){
                lUsername.remove(i);
            }
        }
        cbListUser.setItems(FXCollections.observableArrayList(lUsername));
        cbListUser.getSelectionModel().selectFirst();
        try {
            this.endpoint = new ChatClientEndpoint(username, lvChat);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DeploymentException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void sendMessage(ActionEvent event) throws IOException, EncodeException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeySpecException {
        String receiver = cbListUser.getValue();
        String content = txContent.getText();

        // 1. Khởi tạo khóa AES
        SecretKey key = AESUtil.generateAESKey();

        // 2. Mã hóa nội dung tin nhắn
        byte[] encryptedContent = AESUtil.encryptFile(key, content.getBytes());

        File publicKeyFile = new File("key/"+receiver+"/public_key.txt");

        // 3. Mã hóa khóa AES
        PublicKey publicKey = RSAUtil.getPublicKey(Base64.getDecoder().decode(FileUtil.readBytesFromFile(publicKeyFile)));
        byte[] encryptedKey = RSAUtil.encryptKey(publicKey, key.getEncoded());

        // 4. Nối khóa AES và nội dung tin nhắn đã được mã hóa
        byte[] fileOutputContent = FileUtil.combineBytes(encryptedKey, encryptedContent);

        // convert byte[] to string, then send to receiver
        String mss = Base64.getEncoder().encodeToString(fileOutputContent);


        Message message = new Message(username, receiver, mss);
        this.endpoint.sendMessage(message);
        this.lvChat.getItems().add(message.getFrom() + ": " + content);
        txContent.setText("");
    }

    @FXML
    public void resetChat(ActionEvent event){
        lvChat.getItems().clear();
    }


}
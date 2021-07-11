package client;

import database.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Message;

import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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
    public void sendMessage(ActionEvent event) throws IOException, EncodeException {
        String receiver = cbListUser.getValue();
        String content = txContent.getText();
        Message message = new Message(username, receiver, content);
        this.endpoint.sendMessage(message);
        this.lvChat.getItems().add(message.getFrom() + ": " + message.getContent());
        txContent.setText("");
    }

    @FXML
    public void resetChat(ActionEvent event){
        lvChat.getItems().clear();
    }


}
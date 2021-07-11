package client;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import model.Message;
import model.MessageDecoder;
import model.MessageEncoder;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint(encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class ChatClientEndpoint {
    private Session session = null;
    private ListView<String> lvChat;

    public ChatClientEndpoint(String username,ListView<String> lvChat) throws URISyntaxException, IOException, DeploymentException {
        URI uri = new URI("ws://localhost:8887/ws/chat/" + username);
        this.lvChat = lvChat;
        ContainerProvider.getWebSocketContainer().connectToServer(this, uri);
    }

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
    }

    @OnMessage
    public void onMessage(Message message){
        Platform.runLater(
                () -> {
                    this.lvChat.getItems().add(message.getFrom() + ": " + message.getContent());
                }
        );
    }

    public void sendMessage(Message message) throws IOException, EncodeException {
        this.session.getBasicRemote().sendObject(message);
    }
}

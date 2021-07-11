package client;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import mahoa.AESUtil;
import mahoa.FileUtil;
import mahoa.RSAUtil;
import model.Message;
import model.MessageDecoder;
import model.MessageEncoder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.websocket.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

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
    public void onMessage(Message message) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        byte[] content = message.getContent().getBytes();
        System.out.println("content length : " + content.length);

        File privateKeyFile = new File("key/"+message.getTo()+"/private_key.txt");

        // 1. First 256 is encrypted AES key
        byte[] encryptedKey = Arrays.copyOfRange(content, 0, 256);
        System.out.println("done 1");

        // 2. Next 16 bytes for IV
        byte[] ivBytes = Arrays.copyOfRange(content, 256, 272);
        System.out.println("done 2");

        // 3. Remaining bytes is encrypted file content
        byte[] fileBytes = Arrays.copyOfRange(content, 272, content.length);
        System.out.println("done 3");

        // 4. Decrypt the AES key
        PrivateKey rsaPrivate = RSAUtil.getPrivateKey(Base64.getDecoder().decode(FileUtil.readBytesFromFile(privateKeyFile)));
        System.out.println("done 4.1");
        byte[] aesKeyBytes = RSAUtil.decryptKey(rsaPrivate, encryptedKey);
        System.out.println("done 4");

        // 5. Decrypt file content
        SecretKey aesKey = AESUtil.getAESKey(aesKeyBytes);
        IvParameterSpec ivParams = AESUtil.getIVParams(ivBytes);
        System.out.println("done 5.1");

        byte[] decryptedContent = AESUtil.decryptFile(aesKey, ivParams, fileBytes);
        System.out.println("done 5.2");
        String mss = new String(decryptedContent, StandardCharsets.UTF_8);
        System.out.println("msss : " + mss);

        Platform.runLater(
                () -> {
                    this.lvChat.getItems().add(message.getFrom() + ": "  + mss);
                }
        );
    }

    public void sendMessage(Message message) throws IOException, EncodeException {
        this.session.getBasicRemote().sendObject(message);
    }
}

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
        // đọc content của message
        byte[] content = Base64.getDecoder().decode(message.getContent());

        // đọc private key từ file
        File privateKeyFile = new File("key/"+message.getTo()+"/private_key.txt");


        // 1. 256 byte đầu tiên là khóa AES đã mã hóa
        byte[] encryptedKey = Arrays.copyOfRange(content, 0, 256);

        // 2. 16 byte tiếp theo là IV (vector khởi tạo: initialization value)
        byte[] ivBytes = Arrays.copyOfRange(content, 256, 272);

        // 3. Những byte còn lại là nội dung tin nhắn đã được mã hóa
        byte[] fileBytes = Arrays.copyOfRange(content, 272, content.length);

        // 4. Giải mã khóa AES bằng RSA
        PrivateKey rsaPrivate = RSAUtil.getPrivateKey(Base64.getDecoder().decode(FileUtil.readBytesFromFile(privateKeyFile)));
        byte[] aesKeyBytes = RSAUtil.decryptKey(rsaPrivate, encryptedKey);

        // 5. Giải mã tin nhắn bằng AES
        SecretKey aesKey = AESUtil.getAESKey(aesKeyBytes);
        IvParameterSpec ivParams = AESUtil.getIVParams(ivBytes);
        byte[] decryptedContent = AESUtil.decryptFile(aesKey, ivParams, fileBytes);

        // end :  convert byte[] to string
        String mss = new String(decryptedContent, StandardCharsets.UTF_8);

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

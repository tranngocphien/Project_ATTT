package server;

import model.Message;
import model.MessageDecoder;
import model.MessageEncoder;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@ServerEndpoint(value = "/chat/{username}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class ChatServerEndpoint {
    private Session session;
    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());
    private static Map<String, String> users = new HashMap<>();
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username){
        this.session = session;
        this.sessions.add(session);
        users.put(session.getId(), username);
        System.out.println(username +" connected");

    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
        for(Session s : sessions){
            String session_id = s.getId();
            String user = users.get(session_id);
            if(message.getTo().equals(user)){
                s.getBasicRemote().sendObject(message);
            }
            System.out.println("from: " +message.getFrom()+" - to: "+message.getTo());
            System.out.println("messages: " + message.getContent());

        }
//        broadcast(message);

    }

    @OnClose
    public void onClose(Session session){
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        throwable.printStackTrace();
    }

    private static void broadcast(Message message)
            throws IOException, EncodeException {

        for (Session session: sessions) {
            session.getBasicRemote().sendObject(message);
        }
    }
}

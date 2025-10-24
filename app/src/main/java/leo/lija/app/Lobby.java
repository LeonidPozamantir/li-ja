package leo.lija.app;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import leo.lija.app.db.MessageRepo;
import leo.lija.app.entities.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class Lobby {

    private final SocketIOServer server;
    private final MessageRepo messageRepo;

    private ConcurrentHashMap<String, String> members = new ConcurrentHashMap<>();

    public void join(SocketIOClient client, String uid) {
        client.joinRoom("lobby");
        members.put(uid, client.getSessionId().toString());
    }

    public void talk(String txt, String u) {
        Message message = messageRepo.add(txt, u);
        notifyAll("talk", Map.of(
            "txt", message.getText(),
            "u", message.getUsername()
        ));
    }

    public void quit(String uid) {
        members.remove(uid);
    }

    public void notifyAll(String t, Map<String, Object> data) {
        Map<String, Object> msg = Map.of(
            "t", t,
            "d", data
        );
        server.getRoomOperations("lobby").sendEvent("lobby/send-message", msg);
    }
}

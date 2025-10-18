package leo.lija.app;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class Lobby {

    private final SocketIOServer server;

    private ConcurrentHashMap<String, String> members = new ConcurrentHashMap<>();

    public void join(SocketIOClient client, String username) {
        if (members.containsKey(username)) client.sendEvent("lobby/cannot-connect", "This username is already used");
        else {
            client.joinRoom("lobby");
            members.put(username, client.getSessionId().toString());
            notifyJoin(username);
        }
    }

    public void notifyJoin(String username) {
        notifyAll("join", username, "has entered the lobby");
    }

    public void talk(String username, String text) {
        notifyAll("talk", username, text);
    }

    public void quit(String username) {
        members.remove(username);
        notifyAll("quit", username, "has left the lobby");
    }

    public void notifyAll(String kind, String user, String text) {
        Map<String, Object> msg = Map.of(
            "kind", kind,
            "user", user,
            "message", text,
            "members", members.keySet()
        );
        server.getRoomOperations("lobby").sendEvent("lobby/send-message", msg);
    }


}

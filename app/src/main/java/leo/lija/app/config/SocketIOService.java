package leo.lija.app.config;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import leo.lija.app.lobby.Lobby;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnWebApplication
@RequiredArgsConstructor
public class SocketIOService {

    private final SocketIOServer server;

    @Setter
    private Lobby lobby;

    private final Map<String, String> sessionToUsername = new ConcurrentHashMap<>();
    private final Map<String, SocketIOClient> usernameToClient = new ConcurrentHashMap<>();
    private final Map<String, String> usernameToHook = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        server.addDisconnectListener(onDisconnected());

        server.addEventListener("lobby/join", JoinForm.class, (client, event, ackSender) -> {
            String sessionId = client.getSessionId().toString();
            sessionToUsername.put(sessionId, event.uid);
            usernameToClient.put(event.uid, client);
            usernameToHook.put(event.uid, event.hook);
            lobby.join(event.uid, event.version, Optional.ofNullable(event.hook).filter(h -> !h.isEmpty()));
        });

        server.addEventListener("lobby/talk", LobbyTalkForm.class, (client, event, ackSender) -> {
            if (event.t.equals("talk")) lobby.talk(event);
        });

        server.start();

    }

    public record JoinForm(String uid, Integer version, String hook) {}

    public record LobbyTalkForm(String t, Data data) {
        public record Data(String txt, String u) {}
    }

    @PreDestroy
    public void destroy() {
        server.stop();
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            String username = sessionToUsername.get(sessionId);
            String hook = usernameToHook.get(username);
            lobby.quit(username, Optional.ofNullable(hook));
            usernameToClient.remove(username);
            sessionToUsername.remove(sessionId);
            usernameToHook.remove(username);
        };
    }

    public void addToRoom(String room, String username) {
        usernameToClient.get(username).joinRoom(room);
    }

    public void sendMessage(String room, Object msg) {
        server.getRoomOperations(room).sendEvent("send-message", msg);
    }

}
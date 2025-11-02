package leo.lija.app.config;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import leo.lija.app.controllers.BaseController;
import leo.lija.app.exceptions.AppException;
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
public class SocketIOService extends BaseController {

    private final SocketIOServer server;

    @Setter
    private Lobby lobby;

    private final Map<String, String> sessionToUid = new ConcurrentHashMap<>();
    private final Map<String, SocketIOClient> uidToClient = new ConcurrentHashMap<>();
    private final Map<String, String> uidToHook = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        server.addDisconnectListener(onDisconnected());

        server.addEventListener("lobby/join", JoinForm.class, (client, event, ackSender) -> {
            String sessionId = client.getSessionId().toString();
            sessionToUid.put(sessionId, event.uid);
            uidToClient.put(event.uid, client);
            uidToHook.put(event.uid, event.hook);
            lobby.join(
                get(Optional.ofNullable(event.uid)).orElseThrow(() -> new AppException("Socket UID missing")),
                Optional.ofNullable(event.version).orElseThrow(() -> new AppException("Socket version missing")),
                get(Optional.ofNullable(event.username)),
                get(Optional.ofNullable(event.hook))
            );
        });

        server.addEventListener("lobby/talk", LobbyTalkForm.class, (client, event, ackSender) -> {
            if (event.t.equals("talk")) lobby.talk(event);
        });

        server.start();

    }

    public record JoinForm(String uid, Integer version, String username, String hook) {}

    public record LobbyTalkForm(String t, Data d) {
        public record Data(String txt, String u) {}
    }

    @PreDestroy
    public void destroy() {
        server.stop();
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            String username = sessionToUid.get(sessionId);
            lobby.quit(username);
            uidToClient.remove(username);
            sessionToUid.remove(sessionId);
            uidToHook.remove(username);
        };
    }

    public void addToRoom(String room, String username) {
        uidToClient.get(username).joinRoom(room);
    }

    public void sendMessage(String room, Object msg) {
        server.getRoomOperations(room).sendEvent("send-message", msg);
    }

    public void sendMessageToClient(String username, Object msg) {
        SocketIOClient client = uidToClient.get(username);
        if (client != null) client.sendEvent("send-message", msg);
    }

}
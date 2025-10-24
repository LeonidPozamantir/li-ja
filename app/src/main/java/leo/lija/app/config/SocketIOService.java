package leo.lija.app.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import leo.lija.app.Lobby;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnWebApplication
@RequiredArgsConstructor
public class SocketIOService {

    private final SocketIOServer server;
    private final Lobby lobby;

    private Map<String, String> sessionToUsername = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        server.addDisconnectListener(onDisconnected());

        server.addEventListener("lobby/join", String.class, (client, data, ackSender) -> {
            String sessionId = client.getSessionId().toString();
            sessionToUsername.put(sessionId, data);
            lobby.join(client, data);
        });

        server.addEventListener("lobby/talk", LobbyTalkForm.class, (client, data, ackSender) -> {
            if (data.t.equals("talk")) lobby.talk(data.data.txt, data.data.u);
        });

        server.start();
    }

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
            lobby.quit(sessionToUsername.get(sessionId));
            sessionToUsername.remove(sessionId);
        };
    }

}
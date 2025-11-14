package leo.lija.app.config;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import leo.lija.app.controllers.BaseController;
import leo.lija.app.exceptions.AppException;
import leo.lija.app.lobby.Socket;
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
    private Socket lobbySocket;
    @Setter
    private leo.lija.app.game.Socket gameSocket;

    private final Map<String, String> sessionToUid = new ConcurrentHashMap<>();
    private final Map<String, SocketIOClient> uidToClient = new ConcurrentHashMap<>();
    private final Map<String, String> uidToHook = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        server.addDisconnectListener(onDisconnected());

        server.addEventListener("lobby/join", LobbyJoinForm.class, (client, event, ackSender) -> {
            String sessionId = client.getSessionId().toString();
            sessionToUid.put(sessionId, event.uid);
            uidToClient.put(event.uid, client);
            uidToHook.put(event.uid, event.hook);
            lobbySocket.join(
                get(Optional.ofNullable(event.uid)).orElseThrow(() -> new AppException("Socket UID missing")),
                Optional.ofNullable(event.version).orElseThrow(() -> new AppException("Socket version missing")),
                get(Optional.ofNullable(event.username)),
                get(Optional.ofNullable(event.hook))
            );
        });

        server.addEventListener("lobby/talk", LobbyTalkForm.class, (client, event, ackSender) ->
            lobbySocket.talk(event)
        );

        server.addEventListener("game/join", GameJoinForm.class, (client, event, ackSender) ->
            gameSocket.join(
                event.gameId,
                event.color,
                get(Optional.ofNullable(event.uid)).orElseThrow(() -> new AppException("Socket UID missing")),
                Optional.ofNullable(event.version).orElseThrow(() -> new AppException("Socket version missing")),
                get(Optional.ofNullable(event.playerId)),
                get(Optional.ofNullable(event.username))
            )
        );

        server.addEventListener("game/talk", GameTalkForm.class, (client, event, ackSender) -> {
            String sessionId = client.getSessionId().toString();
            String uid = sessionToUid.get(sessionId);
            gameSocket.talk(uid, event);
        });

        server.addEventListener("game/move", GameMoveForm.class, (client, event, ackSender) ->
            gameSocket.move(event)
        );

        server.start();

    }

    public record LobbyJoinForm(String uid, Integer version, String username, String hook) {}

    public record LobbyTalkForm(String t, Data d) {
        public record Data(String txt, String u) {}
    }

    public record GameJoinForm(String gameId, String color, String uid, Integer version, String playerId, String username) {}

    public record GameTalkForm(String gameId, String t, String d) {}

    public record GameMoveForm(Data d) {
        public record Data(String from, String to) {}
    }

    @PreDestroy
    public void destroy() {
        server.stop();
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            String uid = sessionToUid.get(sessionId);
            lobbySocket.quit(uid);
            uidToClient.remove(uid);
            sessionToUid.remove(sessionId);
            uidToHook.remove(uid);
        };
    }

    public void addToRoom(String room, String username) {
        uidToClient.get(username).joinRoom(room);
    }

    public void removeFromRoom(String room, String username) {
        uidToClient.get(username).leaveRoom(room);
    }

    public void sendMessage(String room, Object msg) {
        server.getRoomOperations(room).sendEvent("send-message", msg);
    }

    public void sendMessageToClient(String uid, String room, Object msg) {
        SocketIOClient client = uidToClient.get(uid);
        if (client != null) client.sendEvent("send-message", room, msg);
    }

}
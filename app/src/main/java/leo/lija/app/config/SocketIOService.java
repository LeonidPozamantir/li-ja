package leo.lija.app.config;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import leo.lija.app.controllers.BaseController;
import leo.lija.app.entities.PovRef;
import leo.lija.app.lobby.Socket;
import leo.lija.app.socket.Util;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnWebApplication
@RequiredArgsConstructor
public class SocketIOService extends BaseController {

    private final SocketIOServer server;

    @Setter
    private Socket lobbySocket;
    @Setter
    private leo.lija.app.site.Socket siteSocket;
    @Setter
    private leo.lija.app.game.Socket gameSocket;

    private final Map<String, String> sessionToUid = new ConcurrentHashMap<>();
    private final Map<String, SocketIOClient> uidToClient = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        server.addDisconnectListener(onDisconnected());

        server.addEventListener("lobby/join", LobbyJoinForm.class, (client, event, ackSender) -> {
            String sessionId = client.getSessionId().toString();
            sessionToUid.put(sessionId, event.uid);
            uidToClient.put(event.uid, client);
            lobbySocket.join(
                get(Optional.ofNullable(event.uid)),
                Optional.ofNullable(event.version),
                get(Optional.ofNullable(event.hook))
            );
        });

        server.addEventListener("site/join", SiteJoinForm.class, (client, event, ackSender) ->
            siteSocket.join(
                get(Optional.ofNullable(event.uid)),
                get(Optional.ofNullable(event.username))
        ));

        server.addEventListener("site/p", String.class, (client, event, ackSender) ->
            siteSocket.ping(event)
        );

        server.addEventListener("lobby/talk", LobbyTalkForm.class, (client, event, ackSender) ->
            lobbySocket.talk(event)
        );

        server.addEventListener("game/join", GameJoinForm.class, (client, event, ackSender) ->
            gameSocket.join(
                event.gameId,
                event.color,
                get(Optional.ofNullable(event.uid)),
                Optional.ofNullable(event.version),
                get(Optional.ofNullable(event.playerId))
            )
        );

        server.addEventListener("game/talk", GameTalkForm.class, (client, event, ackSender) -> {
            String sessionId = client.getSessionId().toString();
            String uid = sessionToUid.get(sessionId);
            gameSocket.talk(uid, event);
        });

        server.addEventListener("game/move", GameMoveForm.class, (client, event, ackSender) -> {
            String sessionId = client.getSessionId().toString();
            String uid = sessionToUid.get(sessionId);
            gameSocket.move(uid, event);
        });

        server.addEventListener("game/moretime", GameMoretimeForm.class, (client, event, ackSender) -> {
            String sessionId = client.getSessionId().toString();
            String uid = sessionToUid.get(sessionId);
            gameSocket.moretime(uid, event);
        });

        server.addEventListener("game/outoftime", GameOutoftimeForm.class, (client, event, ackSender) -> {
            String sessionId = client.getSessionId().toString();
            String uid = sessionToUid.get(sessionId);
            gameSocket.outoftime(uid, event);
        });

        server.start();

    }

    public record LobbyJoinForm(String uid, Integer version, String hook) {}

    public record LobbyTalkForm(Data d) {
        public record Data(String txt, String u) {}
    }

    public record SiteJoinForm(String uid, String username) {}

    public record GameJoinForm(String gameId, String color, String uid, Integer version, String playerId) {}

    public record GameTalkForm(PovRef povRef, Data d) {
        public record Data(String txt) {}
    }

    public record GameMoveForm(PovRef povRef, Data d) {
        public record Data(String from, String to, @Nullable String promotion, @Nullable Integer b) {}
    }

    public record GameMoretimeForm(PovRef povRef) {}

    public record GameOutoftimeForm(PovRef povRef) {}

    @PreDestroy
    public void destroy() {
        server.stop();
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            String uid = sessionToUid.get(sessionId);
            lobbySocket.quit(uid);
            siteSocket.quit(uid);
            Set<String> gameRooms = client.getAllRooms();
            gameRooms.remove("lobby");
            gameRooms.remove("site");
            gameSocket.quit(uid, gameRooms);
            uidToClient.remove(uid);
            sessionToUid.remove(sessionId);
        };
    }

    public void addToRoom(String room, String uid) {
        uidToClient.get(uid).joinRoom(room);
    }

    public void removeFromRoom(String room, String uid) {
        uidToClient.get(uid).leaveRoom(room);
    }

    public void sendMessage(String room, Object msg) {
        server.getRoomOperations(room).sendEvent("send-message", msg);
    }

    public void sendMessageToClient(String uid, String room, Object msg) {
        SocketIOClient client = uidToClient.get(uid);
        if (client != null) client.sendEvent("send-message", room, msg);
    }

}
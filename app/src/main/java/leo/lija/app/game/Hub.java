package leo.lija.app.game;

import leo.lija.app.config.SocketIOService;
import leo.lija.app.entities.event.Event;
import leo.lija.app.socket.History;
import leo.lija.chess.Color;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Hub {

    private final SocketIOService socketService;
    private final String gameId;
    private final History history;

    private final Map<String, Member> members = new ConcurrentHashMap<>();

    public Member getMember(String uid) {
        return members.get(uid);
    }

    public Hub(SocketIOService socketService, String gameId, History history) {
        this.socketService = socketService;
        this.gameId = gameId;
        this.history = history;
    }

    public int getVersion() {
        return history.version();
    }

    public void join(String uid, Integer version, Color color, boolean owner, Optional<String> username) {
        socketService.addToRoom(gameId, uid);
        history.since(version).forEach(m -> socketService.sendMessageToClient(uid, gameId, m));
        members.put(uid, Member.apply(uid, color, owner, username));
    }

    public void events(List<Event> events) {
        events.forEach(this::notifyEvent);
    }

    public void quit(String uid) {
        members.remove(uid);
        socketService.removeFromRoom(gameId, uid);
    }

    public void notifyEvent(Event e) {
        notifyVersion(e.typ(), e.data());
    }

    private void notifyMember(String t, Object data, Member member) {
        Map<String, Object> msg = Map.of(
                "t", t,
                "d", data
        );
        socketService.sendMessageToClient(member.getUid(), gameId, msg);
    }

    private void notifyAll(String t, Object data) {
        Map<String, Object> msg = makeMessage(t, data);
        socketService.sendMessage(gameId, msg);
    }

    private void notifyVersion(String t, Object data) {
        Map<String, Object> vmsg = history.add(makeMessage(t, data));
        socketService.sendMessage(gameId, vmsg);
    }

    private Map<String, Object> makeMessage(String t, Object data) {
        return Map.of(
                "t", t,
                "d", data
        );
    }
}

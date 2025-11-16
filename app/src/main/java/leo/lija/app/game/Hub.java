package leo.lija.app.game;

import leo.lija.app.config.SocketIOService;
import leo.lija.app.entities.PovRef;
import leo.lija.app.entities.event.Event;
import leo.lija.app.socket.History;
import leo.lija.chess.Color;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

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

    public void withMembers(Consumer<Collection<Member>> op) {
        op.accept(members.values());
    }

    public int getVersion() {
        return history.version();
    }

    public CompletableFuture<Integer> getNbMembers() {
        return CompletableFuture.supplyAsync(members::size);
    }

    public void nbPlayers(int nb) {
        notifyAll("nbp", nb);
    }

    public void join(String uid, Integer version, Color color, boolean owner, Optional<String> username) {
        socketService.addToRoom(gameId, uid);
        history.since(version).forEach(m -> socketService.sendMessageToClient(uid, gameId, m));
        members.put(uid, Member.apply(uid, new PovRef(gameId, color), owner, username));
    }

    public void events(List<Event> events) {
        events.forEach(this::notifyVersion);
    }

    public void quit(String uid) {
        members.remove(uid);
        socketService.removeFromRoom(gameId, uid);
    }

    public void notifyVersion(Event e) {
        Map<String, Object> vmsg = history.add(makeMessage(e.typ(), e.data()));
        Collection<Member> m1 = e.owner() ? members.values().stream().filter(Member::isOwner).toList() : members.values();
        Collection<Member> m2 = e.only().map(color -> (Collection<Member>) m1.stream().filter(m -> m.color() == color).toList()).orElse(m1);
        m2.forEach(m -> socketService.sendMessageToClient(m.getUid(), gameId, vmsg));
    }

    private void notifyAll(String t, Object data) {
        Map<String, Object> msg = makeMessage(t, data);
        socketService.sendMessage(gameId, msg);
    }

    private Map<String, Object> makeMessage(String t, Object data) {
        return Map.of(
                "t", t,
                "d", data
        );
    }
}

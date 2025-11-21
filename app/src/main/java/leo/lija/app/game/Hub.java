package leo.lija.app.game;

import leo.lija.app.config.SocketIOService;
import leo.lija.app.entities.PovRef;
import leo.lija.app.entities.event.CrowdEvent;
import leo.lija.app.entities.event.Event;
import leo.lija.chess.Color;
import org.springframework.core.task.TaskExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;

public class Hub implements leo.lija.app.Hub {

    private final TaskExecutor executor;

    private final SocketIOService socketService;
    private final String gameId;
    private final History history;

    private final Map<String, Member> members = new ConcurrentHashMap<>();

    public Member getMember(String uid) {
        return members.get(uid);
    }

    public Hub(TaskExecutor executor, SocketIOService socketService, String gameId, History history) {
        this.executor = executor;
        this.socketService = socketService;
        this.gameId = gameId;
        this.history = history;
    }

    public void withMembers(Consumer<Collection<Member>> op) {
        op.accept(members.values());
    }

    @Override
    public CompletableFuture<List<String>> getUsernames() {
        return CompletableFuture.completedFuture(usernames());
    }

    public void ifEmpty(Runnable op) {
        if (members.isEmpty()) op.run();
    }

    public int getVersion() {
        return history.version();
    }

    public CompletableFuture<Integer> getNbMembers() {
        return CompletableFuture.supplyAsync(members::size, executor);
    }

    @Override
    public CompletableFuture<Void> nbPlayers(int nb) {
        return CompletableFuture.runAsync(() -> notifyAll("nbp", nb), executor);
    }

    public boolean isConnected(Color color) {
        return member(color).isPresent();
    }

    public void join(String uid, Integer version, Color color, boolean owner, Optional<String> username) {
        socketService.addToRoom(gameId, uid);
        List<History.VersionedEvent> msgs = history.since(version).stream().filter(m -> m.visible(color, owner)).toList();
        msgs.forEach(m -> socketService.sendMessageToClient(uid, gameId, m));
        members.put(uid, Member.apply(uid, new PovRef(gameId, color), owner, username));
        notify(crowdEvent());
    }

    public void events(List<Event> events) {
        if (events.size() == 1) notify(events.getFirst());
        if (events.size() > 1) notify(events);
    }

    public void quit(String uid) {
        members.remove(uid);
        socketService.removeFromRoom(gameId, uid);
        notify(crowdEvent());
    }

    private CrowdEvent crowdEvent() {
        return new CrowdEvent(
            member(WHITE).isPresent(),
            member(BLACK).isPresent(),
            (int) members.values().stream().filter(Member::watcher).count()
        );
    }

    private void notify(Event e) {
        History.VersionedEvent vevent = history.add(e);
        members.values().stream().filter(vevent::visible).forEach(m -> socketService.sendMessageToClient(m.getUid(), gameId, vevent));
    }

    private void notify(List<Event> events) {
        List<History.VersionedEvent> vevents = events.stream().map(history::add).toList();
        members.values().forEach(member ->
            socketService.sendMessageToClient(member.getUid(), gameId, Map.of(
                "t", "batch",
                "d", vevents.stream().filter(v -> v.visible(member))
            )));
    }

    private void notifyAll(String t, Object data) {
        Map<String, Object> msg = makeMessage(t, data);
        socketService.sendMessage(gameId, msg);
    }

    private Optional<Member> member(Color color) {
        return members.values().stream().filter(m -> m.isOwner() && m.color() == color).findAny();
    }

    private List<String> usernames() {
        return members.values().stream()
            .filter(m -> m.getUsername().isPresent())
            .map(m -> m.getUsername().get())
            .toList();
    }

    private Map<String, Object> makeMessage(String t, Object data) {
        return Map.of(
                "t", t,
                "d", data
        );
    }
}

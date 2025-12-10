package leo.lija.app.game;

import leo.lija.app.Utils;
import leo.lija.app.config.SocketIOService;
import leo.lija.app.entities.PovRef;
import leo.lija.app.entities.event.CrowdEvent;
import leo.lija.app.entities.event.Event;
import leo.lija.app.socket.HubActor;
import leo.lija.chess.Color;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;

@Slf4j
public class Hub extends HubActor<Member> {

    private final HubMaster hubMaster;
    private final String gameId;
    private final History history;
    private final int hubTimeout;

    private long lastPingTime = Utils.nowMillis();

    public Member getMember(String uid) {
        return members.get(uid);
    }

    public Hub(HubMaster hubMaster, SocketIOService socketService, String gameId, History history, int uidTimeout, int hubTimeout) {
        super(socketService, uidTimeout, gameId);
        this.hubMaster = hubMaster;
        this.gameId = gameId;
        this.history = history;
        this.hubTimeout = hubTimeout;
    }

    @Override
    public void ping(String uid) {
        super.ping(uid);
        lastPingTime = Utils.nowMillis();
    }

    public void broom() {
        super.broom();
        if (lastPingTime < Utils.nowMillis() - hubTimeout * 1000) hubMaster.closeGame(gameId);
    }

    public int getGameVersion() {
        return history.version();
    }

    public boolean isConnectedOnGame(Color color) {
        return member(color).isPresent();
    }

    public void join(String uid, Integer version, Color color, boolean owner) {
        socketService.addToRoom(gameId, uid);
        List<History.VersionedEvent> msgs = history.since(version).stream().filter(m -> m.visible(color, owner)).toList();
        msgs.forEach(m -> socketService.sendMessageToClient(uid, gameId, m));
        addMember(uid, Member.apply(uid, new PovRef(gameId, color), owner));
        notify(crowdEvent());
    }

    public void events(List<Event> events) {
        applyEvents(events);
    }

    @Override
    public void quit(String uid) {
        super.quit(uid);
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

    private void applyEvents(List<Event> events) {
        if (events.size() == 1) notify(events.getFirst());
        if (events.size() > 1) notify(events);
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

    private Optional<Member> member(Color color) {
        return members.values().stream().filter(m -> m.isOwner() && m.color() == color).findAny();
    }

}

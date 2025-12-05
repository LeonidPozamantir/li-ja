package leo.lija.app.game;

import jakarta.annotation.PostConstruct;
import leo.lija.app.Hand;
import leo.lija.app.Messenger;
import leo.lija.app.config.SocketIOService;
import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Progress;
import leo.lija.app.entities.event.Event;
import leo.lija.app.socket.Util;
import leo.lija.chess.Color;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Service("gameSocket")
public class Socket {

    private final TaskScheduler taskScheduler;

    private final Function<String, Optional<DbGame>> getGame;
    private final Hand hand;
    private final HubMemo hubMemo;
    private final Messenger messenger;
    private final SocketIOService socketIOService;

    public Socket(TaskScheduler taskScheduler, GameRepo gameRepo, Hand hand, HubMemo hubMemo, Messenger messenger, SocketIOService socketIOService) {
        this.taskScheduler = taskScheduler;
        this.getGame = gameRepo::game;
        this.hand = hand;
        this.hubMemo = hubMemo;
        this.messenger = messenger;
        this.socketIOService = socketIOService;
    }

    @PostConstruct
    private void init() {
        socketIOService.setGameSocket(this);
    }

    public void send(Progress progress) {
        send(progress.game().getId(), progress.events());
    }

    public void send(String gameId, List<Event> events) {
        hubMemo.get(gameId).events(events);
    }

    public void join(
        String gameId,
        String colorName,
        Optional<String> uidOption,
        Optional<Integer> versionOption,
        Optional<String> playerId
    ) {
        if (uidOption.isPresent() && versionOption.isPresent()) {
            getGame.apply(gameId).ifPresent(gameOption -> Color.apply(colorName).ifPresent(color -> {
                Hub hub = hubMemo.get(gameId);
                hub.join(uidOption.get(), versionOption.get(), color, playerId.flatMap(gameOption::player).isPresent());
            }));
        } else Util.connectionFail();
    }

    public void talk(String uid, SocketIOService.GameTalkForm event) {
        String gameId = event.povRef().gameId();
        Hub hub = hubMemo.get(gameId);
        Member member = hub.getMember(uid);
        if (member instanceof Owner) hub.events(
            messenger.playerMessage(event.povRef(), event.d().txt())
        );
    }

    public void move(String uid, SocketIOService.GameMoveForm event) {
        String gameId = event.povRef().gameId();
        Hub hub = hubMemo.get(gameId);
        Member member = hub.getMember(uid);
        if (!(member instanceof Owner)) return;

        String orig = event.d().from();
        String dest = event.d().to();
        Optional<String> promotion = Optional.ofNullable(event.d().promotion());
        boolean blur = event.d().b() != null && event.d().b() == 1;
        send(gameId, hand.play(event.povRef(), orig, dest, promotion, blur));
    }

    public void moretime(String uid, SocketIOService.GameMoretimeForm event) {
        String gameId = event.povRef().gameId();
        Hub hub = hubMemo.get(gameId);
        Member member = hub.getMember(uid);
        if (!(member instanceof Owner)) return;

        List<Event> events = hand.moretime(event.povRef());
        hub.events(events);
    }

    public void outoftime(String uid, SocketIOService.GameOutoftimeForm event) {
        String gameId = event.povRef().gameId();
        Hub hub = hubMemo.get(gameId);
        Member member = hub.getMember(uid);
        if (!(member instanceof Owner)) return;

        List<Event> events = hand.outoftime(event.povRef());
        hub.events(events);
    }

    public void ping(String gameId, String uid) {
        Hub hub = hubMemo.get(gameId);
        hub.ping(uid);
    }

    public void quit(String uid, Set<String> games) {
        games.forEach(gameId -> {
            Hub hub = hubMemo.get(gameId);
            hub.quit(uid);
        });
    }

}

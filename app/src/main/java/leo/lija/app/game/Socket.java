package leo.lija.app.game;

import jakarta.annotation.PostConstruct;
import leo.lija.app.Hand;
import leo.lija.app.Messenger;
import leo.lija.app.config.SocketIOService;
import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.Progress;
import leo.lija.app.entities.event.Event;
import leo.lija.chess.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service("gameSocket")
@RequiredArgsConstructor
public class Socket {

    private final TaskScheduler taskScheduler;

    private final GameRepo gameRepo;
    private final Hand hand;
    private final HubMemo hubMemo;
    private final Messenger messenger;
    private final SocketIOService socketIOService;

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
        String uid,
        Integer version,
        Optional<String> playerId
    ) {
        gameRepo.gameOption(gameId).ifPresent(gameOption -> Color.apply(colorName).ifPresent(color -> {
            Hub hub = hubMemo.get(gameId);
            hub.join(uid, version, color, playerId.flatMap(gameOption::player).isPresent());
        }));
    }

    public void talk(String uid, SocketIOService.GameTalkForm event) {
        String gameId = event.povRef().gameId();
        Hub hub = hubMemo.get(gameId);
        Member member = hub.getMember(uid);
        if (member instanceof Owner && event.t().equals("talk")) hub.events(
            messenger.playerMessage(event.povRef(), event.d())
        );
    }

    public void move(SocketIOService.GameMoveForm event) {
        String orig = event.d().from();
        String dest = event.d().to();
        Optional<String> promotion = Optional.ofNullable(event.d().promotion());
        send(event.povRef().gameId(), hand.play(event.povRef(), orig, dest, promotion));
    }

    public void moretime(SocketIOService.GameMoretimeForm event) {
        List<Event> events = hand.moretime(event.povRef());
        String gameId = event.povRef().gameId();
        Hub hub = hubMemo.get(gameId);
        hub.events(events);
    }

    public void outoftime(SocketIOService.GameOutoftimeForm event) {
        List<Event> events = hand.outoftime(event.povRef());
        String gameId = event.povRef().gameId();
        Hub hub = hubMemo.get(gameId);
        hub.events(events);
    }

    public void quit(String uid, Set<String> games) {
        games.forEach(gameId -> {
            Hub hub = hubMemo.get(gameId);
            hub.quit(uid);
            scheduleForDeletion(hub, gameId);
        });
    }

    private void scheduleForDeletion(Hub hub, String gameId) {
        taskScheduler.schedule(() ->
            hub.ifEmpty(() -> hubMemo.remove(gameId)), Instant.now().plusSeconds(60)
        );
    }

}

package leo.lija.app.game;

import jakarta.annotation.PostConstruct;
import leo.lija.app.Messenger;
import leo.lija.app.config.SocketIOService;
import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.Progress;
import leo.lija.chess.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("gameSocket")
@RequiredArgsConstructor
public class Socket {

    private final GameRepo gameRepo;
    private final HubMemo hubMemo;
    private final Messenger messenger;
    private final SocketIOService socketIOService;

    @PostConstruct
    private void init() {
        socketIOService.setGameSocket(this);
    }

    public void send(Progress progress) {
        hubMemo.get(progress.game().getId()).events(progress.events());
    }

    public void join(
        String gameId,
        String colorName,
        String uid,
        Integer version,
        Optional<String> playerId,
        Optional<String> username
    ) {
        gameRepo.gameOption(gameId).ifPresent(gameOption -> Color.apply(colorName).ifPresent(color -> {
            Hub hub = hubMemo.get(gameId);
            hub.join(uid, version, color, playerId.flatMap(gameOption::player).isPresent(), username);
        }));
    }

    public void talk(String uid, SocketIOService.GameTalkForm event) {
        String gameId = event.gameId();
        Hub hub = hubMemo.get(gameId);
        Member member = hub.getMember(uid);
        if (member instanceof Owner && event.t().equals("talk")) hub.events(
            messenger.playerMessage(gameId, member.color, event.d())
        );
    }

}

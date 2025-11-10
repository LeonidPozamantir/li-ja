package leo.lija.app.lobby;

import leo.lija.app.IOTools;
import leo.lija.app.Messenger;
import leo.lija.app.Starter;
import leo.lija.app.db.GameRepo;
import leo.lija.app.db.HookRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Evented;
import leo.lija.app.entities.Hook;
import leo.lija.app.memo.AliveMemo;
import leo.lija.chess.Color;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Api extends IOTools {

    private final HookRepo hookRepo;
    private final Fisherman fisherman;
    private final Messenger messenger;
    private final Starter starter;
    private final Socket lobbySocket;
    private final AliveMemo aliveMemo;

    Api(HookRepo hookRepo, Fisherman fisherman, Messenger messenger, Starter starter, Socket lobbySocket, GameRepo gameRepo, AliveMemo aliveMemo) {
        super(gameRepo);
        this.hookRepo = hookRepo;
        this.fisherman = fisherman;
        this.messenger = messenger;
        this.starter = starter;
        this.lobbySocket = lobbySocket;
        this.aliveMemo = aliveMemo;
    }

    public void cancel(String ownerId) {
        Optional<Hook> hook = hookRepo.findByOwnerId(ownerId);
        hook.ifPresent(fisherman::delete);
    }

    public void join(
            String gameId,
            String colorName,
            String entryData,
            String messageString,
            String hookOwnerId,
            Optional<String> myHookOwnerId
    ) {
        Optional<Hook> hook = hookRepo.findByOwnerId(hookOwnerId);
        Color color = ioColor(colorName);
        DbGame game = gameRepo.game(gameId);
        Evented e1 = starter.start(game, entryData);
        e1.addAll(messenger.systemMessages(game, messageString));
        save(e1);
        aliveMemo.put(gameId, color);
        aliveMemo.put(gameId, color.getOpposite());
        hook.ifPresent(h -> fisherman.bite(h, e1.game()));
        myHookOwnerId.ifPresent(ownerId -> hookRepo.findByOwnerId(ownerId)
                .ifPresent(fisherman::delete));
    }

    public void create(String hookOwnerId) {
        Optional<Hook> hook = hookRepo.findByOwnerId(hookOwnerId);
        hook.ifPresent(fisherman::add);
    }

}

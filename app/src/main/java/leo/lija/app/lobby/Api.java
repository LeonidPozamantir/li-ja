package leo.lija.app.lobby;

import leo.lija.app.IOTools;
import leo.lija.app.Messenger;
import leo.lija.app.Starter;
import leo.lija.app.entities.Hook;
import leo.lija.chess.Color;
import leo.lija.app.db.GameRepo;
import leo.lija.app.db.HookRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.memo.AliveMemo;
import leo.lija.app.memo.VersionMemo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Api extends IOTools {

    private final HookRepo hookRepo;
    private final Fisherman fisherman;
    private final Messenger messenger;
    private final Starter starter;
    private final Lobby lobbySocket;
    private final AliveMemo aliveMemo;

    Api(HookRepo hookRepo, Fisherman fisherman, Messenger messenger, Starter starter, Lobby lobbySocket, VersionMemo versionMemo, GameRepo gameRepo, AliveMemo aliveMemo) {
        super(gameRepo, versionMemo);
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
        messenger.systemMessages(game, messageString);
        starter.start(game, entryData);
        save(game);
        aliveMemo.put(gameId, color);
        aliveMemo.put(gameId, color.getOpposite());
        hook.ifPresent(h -> fisherman.bite(h, game));
        myHookOwnerId.ifPresent(ownerId -> hookRepo.findByOwnerId(ownerId)
                .ifPresent(fisherman::delete));
    }

    public void create(String hookOwnerId) {
        Optional<Hook> hook = hookRepo.findByOwnerId(hookOwnerId);
        hook.ifPresent(fisherman::add);
    }

}

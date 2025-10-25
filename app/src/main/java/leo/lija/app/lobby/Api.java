package leo.lija.app.lobby;

import leo.lija.app.IOTools;
import leo.lija.app.Messenger;
import leo.lija.app.Starter;
import leo.lija.chess.Color;
import leo.lija.app.db.GameRepo;
import leo.lija.app.db.HookRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.memo.AliveMemo;
import leo.lija.app.memo.HookMemo;
import leo.lija.app.memo.LobbyMemo;
import leo.lija.app.memo.VersionMemo;
import org.springframework.stereotype.Service;

@Service
public class Api extends IOTools {

    private final HookRepo hookRepo;
    private final Messenger messenger;
    private final Starter starter;
    private final LobbyMemo lobbyMemo;
    private final AliveMemo aliveMemo;
    private final HookMemo hookMemo;

    Api(HookRepo hookRepo, Messenger messenger, Starter starter, LobbyMemo lobbyMemo, VersionMemo versionMemo, GameRepo gameRepo, AliveMemo aliveMemo, HookMemo hookMemo) {
        super(gameRepo, versionMemo);
        this.hookRepo = hookRepo;
        this.messenger = messenger;
        this.starter = starter;
        this.lobbyMemo = lobbyMemo;
        this.aliveMemo = aliveMemo;
        this.hookMemo = hookMemo;
    }

    public void cancel(String ownerId) {
        hookRepo.deleteByOwnerId(ownerId);
        hookMemo.remove(ownerId);
        versionInc();
    }

    public void join(String gameId, String colorName, String entryData, String messageString) {
        Color color = ioColor(colorName);
        DbGame game = gameRepo.game(gameId);
        messenger.systemMessages(game, messageString);
        starter.start(game, entryData);
        save(game);
        aliveMemo.put(gameId, color);
        aliveMemo.put(gameId, color.getOpposite());
        versionInc();
    }

    public void create(String hookOwnerId) {
        hookMemo.put(hookOwnerId);
        versionInc();
    }

    public void alive(String hookOwnerId) {
        hookMemo.put(hookOwnerId);
    }

    private int versionInc() {
        return lobbyMemo.increase();
    }
}

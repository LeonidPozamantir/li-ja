package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.system.db.GameRepo;
import leo.lija.system.db.HookRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.Hook;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.HookMemo;
import leo.lija.system.memo.LobbyMemo;
import leo.lija.system.memo.VersionMemo;
import org.springframework.stereotype.Service;

@Service
public class LobbyApi extends IOTools {

    private final HookRepo hookRepo;
    private final LobbyMemo lobbyMemo;
    private final AliveMemo aliveMemo;
    private final HookMemo hookMemo;

    LobbyApi(HookRepo hookRepo, LobbyMemo lobbyMemo, VersionMemo versionMemo, GameRepo gameRepo, AliveMemo aliveMemo, HookMemo hookMemo) {
        super(gameRepo, versionMemo);
        this.hookRepo = hookRepo;
        this.lobbyMemo = lobbyMemo;
        this.aliveMemo = aliveMemo;
        this.hookMemo = hookMemo;
    }

    public void join(String gameId, String colorName) {
        Color color = ioColor(colorName);
        DbGame g1 = gameRepo.game(gameId);
        aliveMemo.put(gameId, color);
        aliveMemo.put(gameId, color.getOpposite());
        lobbyMemo.increase();
    }

    public void inc() {
        lobbyMemo.increase();
    }

    public void create(Hook hook) {
        hookRepo.save(hook);
        lobbyMemo.increase();
        hookMemo.put(hook.getOwnerId());
    }
}

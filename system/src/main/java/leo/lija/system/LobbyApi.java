package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.system.db.GameRepo;
import leo.lija.system.db.HookRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.LobbyMemo;
import leo.lija.system.memo.VersionMemo;
import org.springframework.stereotype.Service;

@Service
public class LobbyApi extends IOTools {

    private final HookRepo hookRepo;
    private final LobbyMemo lobbyMemo;
    private final AliveMemo aliveMemo;

    LobbyApi(HookRepo hookRepo, LobbyMemo lobbyMemo, VersionMemo versionMemo, GameRepo gameRepo, AliveMemo aliveMemo) {
        super(gameRepo, versionMemo);
        this.hookRepo = hookRepo;
        this.lobbyMemo = lobbyMemo;
        this.aliveMemo = aliveMemo;
    }

    public void join(String gameId, String colorName) {
        Color color = ioColor(colorName);
        DbGame g1 = gameRepo.game(gameId);
        aliveMemo.put(gameId, color);
        aliveMemo.put(gameId, color.getOpposite());
    }
}

package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.system.db.GameRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.VersionMemo;
import org.springframework.stereotype.Service;

@Service
public class LobbyApi extends IOTools {

    private final AliveMemo aliveMemo;

    LobbyApi(GameRepo gameRepo, VersionMemo versionMemo, AliveMemo aliveMemo) {
        super(gameRepo, versionMemo);
        this.aliveMemo = aliveMemo;
    }

    public void join(String gameId, String colorName) {
        Color color = ioColor(colorName);
        DbGame g1 = gameRepo.game(gameId);
        aliveMemo.put(gameId, color);
        aliveMemo.put(gameId, color.getOpposite());
    }
}

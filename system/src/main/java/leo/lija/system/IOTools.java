package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.system.db.GameRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.exceptions.AppException;
import leo.lija.system.memo.VersionMemo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class IOTools {

    protected final GameRepo gameRepo;
    protected final VersionMemo versionMemo;

    protected Color ioColor(String colorName) {
        return Color.apply(colorName).orElseThrow(() -> new AppException("Invalid color"));
    }

    protected void save(DbGame g1) {
        gameRepo.save(g1);
        versionMemo.put(g1);
    }
}

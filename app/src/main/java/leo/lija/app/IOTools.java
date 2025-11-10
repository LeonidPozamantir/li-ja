package leo.lija.app;

import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Evented;
import leo.lija.app.exceptions.AppException;
import leo.lija.chess.Color;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class IOTools {

    protected final GameRepo gameRepo;

    protected Color ioColor(String colorName) {
        return Color.apply(colorName).orElseThrow(() -> new AppException("Invalid color"));
    }

    protected void save(DbGame g1) {
        gameRepo.save(g1);
    }

    protected void save(Evented evented) {
        gameRepo.save(evented.game());
        // send events
    }
}

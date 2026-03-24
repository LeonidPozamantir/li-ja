package leo.lija.app;

import leo.lija.app.entities.DbGame;
import leo.lija.chess.Eco;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameInfoService {

    private final PgnDump pgnDump;

    public GameInfo apply(DbGame game) {
        return new GameInfo(
            game,
            pgnDump.game2Str(game),
            Eco.openingOf(game.getPgn())
        );
    }
}

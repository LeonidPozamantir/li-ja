package leo.lija.system;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;
import leo.lija.system.exceptions.AppException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StupidAi implements Ai {
    @Override
    public Pair<Game, Move> apply(DbGame dbGame) {
        Game game = dbGame.toChess();
        Pair<Pos, List<Pos>> destination = game.situation().destinations().entrySet().stream()
            .findFirst().map(e -> Pair.of(e.getKey(), e.getValue())).orElseThrow(() -> new AppException("Game is finished"));
        Pos orig = destination.getFirst();
        List<Pos> dests = destination.getSecond();
        Pos dest = dests.stream().findFirst().orElseThrow(() -> new AppException("No moves from " + orig));
        return game.apply(orig, dest);
    }
}

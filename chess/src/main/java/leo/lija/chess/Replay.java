package leo.lija.chess;

import java.util.ArrayList;
import java.util.List;

public record Replay(Game game, List<Move> moves) {

    public static Replay apply(Game game) {
        return new Replay(game, new ArrayList<>());
    }
}

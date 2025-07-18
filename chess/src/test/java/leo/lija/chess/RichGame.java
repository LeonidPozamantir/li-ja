package leo.lija.chess;

import io.vavr.collection.Map;
import leo.lija.chess.utils.Pair;

import java.util.Arrays;
import java.util.Optional;

public class RichGame extends Game {

    public RichGame(Board board, Color player, String pgnMoves, Optional<Clock> clock, Map<Pos, Piece> deads) {
        super(board, player, pgnMoves, clock, deads);
    }

    public RichGame(Board board, Color player) {
        super(board, player);
    }

    public RichGame() {
        super();
    }

    public RichGame as(Color color) {
        return new RichGame(board, color, pgnMoves, clock, deads);
    }

    @SafeVarargs
    public final RichGame playMoves(Pair<Pos, Pos>... moves) {
        return Arrays.stream(moves)
            .reduce(this, (g, move) -> g.playMove(move.getFirst(), move.getSecond()), (s1, s2) -> s1);
    }

    @Override
    public RichGame playMove(Pos from, Pos to) {
        Game game = super.playMove(from, to);
        return new RichGame(game.board, game.player, game.pgnMoves, game.clock, game.deads);
    }

}

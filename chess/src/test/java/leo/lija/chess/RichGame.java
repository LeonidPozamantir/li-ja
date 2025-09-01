package leo.lija.chess;

import io.vavr.collection.List;
import leo.lija.chess.utils.Pair;

import java.util.Optional;

import static leo.lija.chess.Role.QUEEN;

public class RichGame extends Game {

    public RichGame(Board board, Color player, String pgnMoves, Optional<Clock> clock, List<Pair<Pos, Piece>> deads, int turns) {
        super(board, player, pgnMoves, clock, deads, turns);
    }

    public RichGame(Game game) {
        super(game.board, game.player, game.pgnMoves, game.clock, game.deads, game.turns);
    }

    public RichGame(Board board, Color player) {
        super(board, player);
    }

    public RichGame() {
        super();
    }

    public RichGame as(Color color) {
        return new RichGame(board, color, pgnMoves, clock, deads, turns);
    }

    @SafeVarargs
    public final RichGame playMoves(Pair<Pos, Pos>... moves) {
        return playMoveList(java.util.List.of(moves));

    }

    public final RichGame playMoveList(java.util.List<Pair<Pos, Pos>> moves) {
        return moves.stream()
            .reduce(this, (g, move) -> new RichGame(g.apply(move.getFirst(), move.getSecond()).getFirst()), (s1, s2) -> s1);
    }

    public RichGame playMove(Pos from, Pos to) {
        return playMove(from, to, QUEEN);
    }

    public RichGame playMove(Pos from, Pos to, Role promotion) {
        return new RichGame(apply(from, to, promotion).getFirst());
    }

    public RichGame withClock(Clock c) {
        return new RichGame(board, player, pgnMoves, Optional.of(c), deads, turns);
    }

}

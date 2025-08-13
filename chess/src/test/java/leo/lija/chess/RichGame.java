package leo.lija.chess;

import io.vavr.collection.List;
import leo.lija.chess.utils.Pair;

import java.util.Optional;

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
            .reduce(this, (g, move) -> g.playMove(move.getFirst(), move.getSecond()), (s1, s2) -> s1);
    }

    @Override
    public RichGame playMove(Pos from, Pos to) {
        Game game = super.playMove(from, to);
        return new RichGame(game.board, game.player, game.pgnMoves, game.clock, game.deads, game.turns);
    }

}

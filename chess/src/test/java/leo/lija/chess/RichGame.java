package leo.lija.chess;

import leo.lija.chess.utils.Pair;

import java.util.Arrays;

import static leo.lija.chess.Color.WHITE;

public class RichGame extends Game {

    public RichGame(Board board, Color player, String pgnMoves) {
        super(board, player, pgnMoves);
    }

    public RichGame(Board board, Color player) {
        super(board, player);
    }

    public RichGame as(Color color) {
        return new RichGame(board, color, pgnMoves);
    }

    @SafeVarargs
    public final RichGame playMoves(Pair<Pos, Pos>... moves) {
        return Arrays.stream(moves)
            .reduce(this, (g, move) -> g.playMove(move.getFirst(), move.getSecond()), (s1, s2) -> s1);
    }

    @Override
    public RichGame playMove(Pos from, Pos to) {
        Game game = super.playMove(from, to);
        return new RichGame(game.board, game.player, game.pgnMoves);
    }

    public static RichGame newGame() {
        return new RichGame(new Board(), WHITE);
    }
}

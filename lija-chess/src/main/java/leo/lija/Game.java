package leo.lija;

import leo.lija.utils.Pair;

import java.util.Arrays;
import java.util.List;

import static leo.lija.Color.WHITE;
import static leo.lija.Role.QUEEN;

public record Game(
    Board board,
    Color player,
    List<String> pgnMoves
) {

    public Game(Board board, Color player) {
        this(board, player, List.of());
    }
    public Game() {
        this(Board.empty(), WHITE);
    }

    @SafeVarargs
    public final Game playMoves(Pair<Pos, Pos>... moves) {
        return Arrays.stream(moves)
            .reduce(this, (sit, move) -> sit.playMove(move.getFirst(), move.getSecond()), (s1, s2) -> s1);
    }

    public Game playMove(Pos from, Pos to) {
        return playMove(from, to, QUEEN);
    }

    public Game playMove(Pos from, Pos to, Role promotion) {
        Move move =  situation().playMove(from, to, promotion);
        return new Game(move.after(), player.getOpposite());

    }

    public Situation situation() {
        return board.as(player);
    }

    public Game as(Color c) {
        return new Game(board, c, pgnMoves);
    }

    public static Game newGame() {
        return new Game(new Board(), WHITE);
    }
}

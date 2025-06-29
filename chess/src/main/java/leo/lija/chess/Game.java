package leo.lija.chess;

import leo.lija.chess.format.PgnDump;
import leo.lija.chess.utils.Pair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Role.QUEEN;

@RequiredArgsConstructor
public class Game {

    protected final Board board;
    protected final Color player;
    @Getter
    protected final List<String> pgnMoves;

    private Optional<Situation> cachedSituation = Optional.empty();

    public Game(Board board, Color player) {
        this(board, player, new ArrayList<>());
    }

    public Game playMove(Pos from, Pos to) {
        return playMove(from, to, QUEEN);
    }

    public Game playMove(Pos from, Pos to, Role promotion) {
        Move move =  situation().move(from, to, promotion);
        Game newGame = new Game(move.afterWithPositionHashesUpdated(), player.getOpposite());
        String pgnMove = PgnDump.move(situation(), move, newGame.situation());
        pgnMoves.add(pgnMove);
        return new Game(newGame.board, newGame.player, pgnMoves);

    }

    public Situation situation() {
        if (cachedSituation.isEmpty()) cachedSituation = Optional.of(new Situation(board, player));
        return cachedSituation.get();
    }

    public static Game newGame() {
        return new Game(new Board(), WHITE);
    }
}

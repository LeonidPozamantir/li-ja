package leo.lija.chess;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import leo.lija.chess.format.PgnDump;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Role.QUEEN;

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
public class Game {

    protected final Board board;
    protected final Color player;
    protected final String pgnMoves;
    protected final Optional<Clock> clock;
    protected final Map<Pos, Piece> deads;

    private Optional<Situation> cachedSituation = Optional.empty();

    public Game() {
        this(new Board(), WHITE);
    }

    public Game(Board board, Color player) {
        this(board, player, "", Optional.empty(), HashMap.empty());
    }

    public Game(Board board, Color player, String pgnMoves) {
        this(board, player, pgnMoves, Optional.empty(), HashMap.empty());
    }

    public Game playMove(Pos from, Pos to) {
        return playMove(from, to, QUEEN);
    }

    public Game playMove(Pos from, Pos to, Role promotion) {
        Move move =  situation().move(from, to, promotion);
        Game newGame = new Game(move.afterWithPositionHashesUpdated(), player.getOpposite());
        String pgnMove = PgnDump.move(situation(), move, newGame.situation());
        String newPgnMoves = (pgnMoves + " " + pgnMove).trim();
        Optional<Pos> cpos = move.capture();
        Optional<Piece> cpiece = cpos.flatMap(p -> board.at(p));
        Map<Pos, Piece> newDeads = cpiece.isPresent() ? deads.put(cpos.get(), cpiece.get()) : deads;
        return new Game(newGame.board, newGame.player, newPgnMoves, clock, newDeads);
    }

    public Situation situation() {
        if (cachedSituation.isEmpty()) cachedSituation = Optional.of(new Situation(board, player));
        return cachedSituation.get();
    }

    public List<String> pgnMovesList() {
        return Arrays.asList(pgnMoves.split(" "));
    }

}

package leo.lija.chess;

import leo.lija.chess.format.PgnDump;
import leo.lija.chess.utils.Pair;
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
    protected final io.vavr.collection.List<Pair<Pos, Piece>> deads;
    protected final int turns;

    private Optional<Situation> cachedSituation = Optional.empty();

    public Game() {
        this(new Board());
    }

    public Game(Board board) {
        this(board, WHITE);
    }

    public Game(Board board, Color player) {
        this(board, player, "", Optional.empty(), io.vavr.collection.List.empty(), 0);
    }

    public Game(Board board, Color player, String pgnMoves) {
        this(board, player, pgnMoves, Optional.empty(), io.vavr.collection.List.empty(), 0);
    }

    public Game(Board board, Color player, String pgnMoves, Optional<Clock> clock, io.vavr.collection.List<Pair<Pos, Piece>> deads) {
        this(board, player, pgnMoves, clock, deads, 0);
    }

    public Pair<Game, Move> apply(Pos from, Pos to) {
        return apply(from, to, null);
    }

    public Pair<Game, Move> apply(Pos from, Pos to, Role promotion) {
        if (promotion == null) promotion = QUEEN;
        Move move =  situation().move(from, to, promotion);
        Game newGame = new Game(move.finalizeAfter(), player.getOpposite());
        String pgnMove = PgnDump.move(situation(), move, newGame.situation());
        String newPgnMoves = (pgnMoves + " " + pgnMove).trim();
        Optional<Pos> cpos = move.capture();
        Optional<Piece> cpiece = cpos.flatMap(p -> board.at(p));
        io.vavr.collection.List<Pair<Pos, Piece>> newDeads = cpiece.isPresent() ? deads.append(Pair.of(cpos.get(), cpiece.get())) : deads;
        return Pair.of(new Game(newGame.board, newGame.player, newPgnMoves, clock, newDeads, turns + 1), move);
    }

    public Game playMove(Pos from, Pos to) {
        return playMove(from, to, QUEEN);
    }

    public Game playMove(Pos from, Pos to, Role promotion) {
        return apply(from, to, promotion).getFirst();
    }

    public Situation situation() {
        if (cachedSituation.isEmpty()) cachedSituation = Optional.of(new Situation(board, player));
        return cachedSituation.get();
    }

    public List<String> pgnMovesList() {
        return Arrays.asList(pgnMoves.split(" "));
    }

}

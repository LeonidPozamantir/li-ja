package leo.lija;

import leo.lija.exceptions.ChessRulesException;
import leo.lija.format.VisualFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static leo.lija.Color.BLACK;
import static leo.lija.Color.WHITE;
import static leo.lija.Pos.makePos;
import static leo.lija.Role.BISHOP;
import static leo.lija.Role.KING;
import static leo.lija.Role.KNIGHT;
import static leo.lija.Role.QUEEN;
import static leo.lija.Role.ROOK;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Board {

    private static final String NO_PIECE_AT = "No piece at";
    private final VisualFormat visualFormat = new VisualFormat();

    @Getter
    @EqualsAndHashCode.Include
    private final Map<Pos, Piece> pieces;

    @Getter
    @EqualsAndHashCode.Include
    private final History history;

    private Optional<Map<Color, Set<Pos>>> optOccupation = Optional.empty();
    private Optional<Map<Color, List<Actor>>> optColorActors = Optional.empty();
    private Optional<Map<Color, Pos>> optKingPos = Optional.empty();

    public Optional<Piece> at (Pos at) {
        return Optional.ofNullable(pieces.get(at));
    }

    public Optional<Piece> at(int x, int y) {
        return makePos(x, y).flatMap(this::at);
    }

    public Piece pieceAt(Pos at) {
        Optional<Piece> optPiece = at(at);
        if (optPiece.isEmpty()) throw new ChessRulesException(NO_PIECE_AT + " " + at);
        return optPiece.get();
    }

    public Map<Pos, Actor> actors() {
        return pieces.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new Actor(e.getValue(), e.getKey(), this)));
    }

    public Map<Color, List<Actor>> colorActors() {
        if (optColorActors.isEmpty()) {
            Map<Color, List<Actor>> colorActors = actors().values().stream().collect(Collectors.groupingBy(Actor::color));
            optColorActors = Optional.of(colorActors);
        }
        return optColorActors.get();
    }

    public List<Actor> actorsOf(Color color) {
        return colorActors().getOrDefault(color, List.of());
    }

    public Actor actorAt(Pos at) {
        Actor res = actors().get(at);
        if (res == null) throw new ChessRulesException(NO_PIECE_AT + " " + at);
        return res;
    }

    public Optional<Pos> kingPosOf(Color color) {
        if (optKingPos.isEmpty()) {
            Map<Color, Pos> kingPos = pieces.entrySet().stream()
                .filter(e -> e.getValue().role() == KING)
                .collect(Collectors.toMap(e -> e.getValue().color(), Map.Entry::getKey));
            optKingPos = Optional.of(kingPos);
        }
        return Optional.ofNullable(optKingPos.get().get(color));
    }

    public Set<Pos> movesFrom(Pos from) {
        return actorAt(from).destinations();
    }

    public Board placeAt(Piece piece, Pos at) {
        return place(piece, at).orElseThrow(() -> new ChessRulesException("Cannot place to occupied " + at));
    }

    public Optional<Board> place(Piece piece, Pos at) {
        if (pieces.containsKey(at)) return Optional.empty();
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        piecesNew.put(at, piece);
        return Optional.of(new Board(piecesNew, history));
    }

    public Board takeValid(Pos at) {
        return take(at).orElseThrow(() -> new ChessRulesException(NO_PIECE_AT + " " + at + " to move"));
    }

    public Optional<Board> take(Pos at) {
        return Optional.ofNullable(pieces.get(at)).map(p -> {
            Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
            piecesNew.remove(at);
            return new Board(piecesNew, history);
        });
    }

    public Optional<Board> taking(Pos orig, Pos dest) {
        return taking(orig, dest, Optional.empty());
    }

    public Optional<Board> taking(Pos orig, Pos dest, Optional<Pos> taking) {
        Piece piece = pieces.get(orig);
        if (piece == null) return Optional.empty();
        Pos takenPos = taking.orElse(dest);
        if (!pieces.containsKey(takenPos)) return Optional.empty();
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        piecesNew.remove(takenPos);
        piecesNew.remove(orig);
        piecesNew.put(dest, piece);
        return Optional.of(new Board(piecesNew, history));
    }

    public Board moveTo(Pos orig, Pos dest) {
        if (!pieces.containsKey(orig)) throw new ChessRulesException(NO_PIECE_AT + " " + orig + " to move");
        if (pieces.containsKey(dest)) throw new ChessRulesException("Cannot move to occupied " + dest);
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        piecesNew.put(dest, piecesNew.remove(orig));
        return new Board(piecesNew, history);
    }

    public Optional<Board> move(Pos orig, Pos dest) {
        try {
            return Optional.of(moveTo(orig, dest));
        } catch (ChessRulesException e) {
            return Optional.empty();
        }
    }

    public Optional<Board> promote(Pos orig, Pos dest) {
        return Optional.ofNullable(pieces.get(orig))
            .flatMap(pawn -> {
                Optional<Board> b1 = move(orig, dest);
                Optional<Board> b2 = b1.flatMap(b -> b.take(dest));
                return b2.map(b -> b.placeAt(pawn.color().queen(), dest));
            });
    }

    public Board withHistory(History h) {
        return new Board(pieces, h);
    }

    public Board updateHistory(UnaryOperator<History> f) {
        return new Board(pieces, f.apply(history));
    }

    public Situation as(Color c) {
        return new Situation(this, c);
    }

    public long count(Piece p) {
        return pieces.entrySet().stream()
            .filter(e -> e.getValue().equals(p))
            .count();
    }

    public Map<Color, Set<Pos>> occupation() {
        if (optOccupation.isEmpty()) {
            Map<Color, Set<Pos>> occupation = Arrays.stream(Color.values()).collect(Collectors.toMap(
                    Function.identity(),
                    c -> pieces.entrySet().stream()
                            .filter(e -> e.getValue().color() == c)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toSet())
            ));
            optOccupation = Optional.of(occupation);
        }
        return optOccupation.get();
    }

    public Set<Pos> occupations() {
        return pieces.keySet();
    }

    String visual() {
        return visualFormat.obj2Str(this);
    }
    @Override
    public String toString() {
        return visual();
    }

    public Board() {
        Map<Pos, Piece> piecesNew = new HashMap<>();
        List<Role> lineUp = List.of(ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK);
        for (int y: List.of(1, 2, 7, 8)) {
            for (int x = 1; x <= 8; x++) {
                Piece piece = null;
                switch (y) {
                    case 1:
                        piece = WHITE.of(lineUp.get(x - 1));
                        break;
                    case 2:
                        piece = WHITE.pawn();
                        break;
                    case 7:
                        piece = BLACK.pawn();
                        break;
                    case 8:
                        piece = BLACK.of(lineUp.get(x - 1));
                        break;
                    default:
                }
                piecesNew.put(Pos.atUnsafe(x, y), piece);
            }
        }
        this.pieces = piecesNew;
        this.history = new History();
    }

    public Board(Map<Pos, Piece> pieces) {
        this(pieces, new History());
    }

    public static Board empty() {
        return new Board(Map.of());
    }
}

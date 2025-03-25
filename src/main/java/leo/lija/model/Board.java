package leo.lija.model;

import leo.lija.exceptions.ChessRulesException;
import leo.lija.format.Visual;
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
import java.util.stream.Collectors;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Role.BISHOP;
import static leo.lija.model.Role.KING;
import static leo.lija.model.Role.KNIGHT;
import static leo.lija.model.Role.PAWN;
import static leo.lija.model.Role.QUEEN;
import static leo.lija.model.Role.ROOK;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Board {

    private static final String NO_PIECE_AT = "No piece at";
    Visual visual = new Visual();

    @Getter
    @EqualsAndHashCode.Include
    private final Map<Pos, Piece> pieces;

    private Optional<Map<Color, Set<Pos>>> optOccupation = Optional.empty();

    public Optional<Piece> at (Pos at) {
        return Optional.ofNullable(pieces.get(at));
    }

    public Optional<Piece> at(int x, int y) {
        return Pos.at(x, y).flatMap(this::at);
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

    public Actor actorAt(Pos at) {
        Actor res = actors().get(at);
        if (res == null) throw new ChessRulesException(NO_PIECE_AT + " " + at);
        return res;
    }

    public Set<Pos> movesFrom(Pos from) {
        return actorAt(from).moves();
    }

    public Board placeAt(Piece piece, Pos at) {
        if (pieces.containsKey(at)) throw new ChessRulesException("Cannot place to occupied " + at);
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        piecesNew.put(at, piece);
        return new Board(piecesNew);
    }

    public Board take(Pos at) {
        if (!pieces.containsKey(at)) throw new ChessRulesException(NO_PIECE_AT + " " + at + " to move");
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        piecesNew.remove(at);
        return new Board(piecesNew);
    }

    public Board moveTo(Pos orig, Pos dest) {
        if (!pieces.containsKey(orig)) throw new ChessRulesException(NO_PIECE_AT + " " + orig + " to move");
        if (pieces.containsKey(dest)) throw new ChessRulesException("Cannot move to occupied " + dest);
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        piecesNew.put(dest, piecesNew.remove(orig));
        return new Board(piecesNew);
    }

    public Board promoteTo(Pos at, Role role) {
        if (role == PAWN || role == KING) throw new ChessRulesException("Cannot promote to " + role);
        if (!pieces.containsKey(at) || pieces.get(at).role() != PAWN)
            throw new ChessRulesException("No pawn at " + at + " to promote");
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        piecesNew.put(at, new Piece(pieces.get(at).color(), role));
        return new Board(piecesNew);
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

    @Override
    public String toString() {
        return visual.obj2Str(this);
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
    }

    public static Board empty() {
        return new Board(Map.of());
    }
}

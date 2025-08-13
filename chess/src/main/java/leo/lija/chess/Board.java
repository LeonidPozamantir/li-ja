package leo.lija.chess;

import leo.lija.chess.exceptions.ChessException;
import leo.lija.chess.exceptions.ChessRulesException;
import leo.lija.chess.format.VisualFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.posAt;
import static leo.lija.chess.Role.BISHOP;
import static leo.lija.chess.Role.KING;
import static leo.lija.chess.Role.KNIGHT;
import static leo.lija.chess.Role.QUEEN;
import static leo.lija.chess.Role.ROOK;

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

    private Optional<Map<Color, Set<Pos>>> cachedOccupation = Optional.empty();
    private Optional<Map<Color, List<Actor>>> cachedColorActors = Optional.empty();
    private Optional<Map<Color, Pos>> cachedKingPos = Optional.empty();

    public Optional<Piece> at (Pos at) {
        return Optional.ofNullable(pieces.get(at));
    }

    public Optional<Piece> at(int x, int y) {
        return posAt(x, y).flatMap(this::at);
    }

    public Map<Pos, Actor> actors() {
        return pieces.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new Actor(e.getValue(), e.getKey(), this)));
    }

    public Map<Color, List<Actor>> colorActors() {
        if (cachedColorActors.isEmpty()) {
            Map<Color, List<Actor>> colorActors = actors().values().stream().collect(Collectors.groupingBy(Actor::color));
            cachedColorActors = Optional.of(colorActors);
        }
        return cachedColorActors.get();
    }

    public List<Role> rolesOf(Color c) {
        return pieces.values().stream()
            .filter(p -> p.color().equals(c))
            .map(Piece::role)
            .toList();
    }

    public List<Actor> actorsOf(Color color) {
        return colorActors().getOrDefault(color, List.of());
    }

    public Optional<Actor> actorAt(Pos at) {
        return Optional.ofNullable(actors().get(at));
    }

    public Map<Pos, Piece> piecesOf(Color c) {
        return pieces.entrySet().stream().filter(e -> e.getValue().is(c)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Optional<Pos> kingPosOf(Color color) {
        if (cachedKingPos.isEmpty()) {
            Map<Color, Pos> kingPos = pieces.entrySet().stream()
                .filter(e -> e.getValue().role() == KING)
                .collect(Collectors.toMap(e -> e.getValue().color(), Map.Entry::getKey));
            cachedKingPos = Optional.of(kingPos);
        }
        return Optional.ofNullable(cachedKingPos.get().get(color));
    }

    public Optional<List<Pos>> destsFrom(Pos from) {
        return actorAt(from).map(Actor::destinations);
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

    public Map<Color, Set<Pos>> occupation() {
        if (cachedOccupation.isEmpty()) {
            Map<Color, Set<Pos>> occupation = Arrays.stream(Color.values()).collect(Collectors.toMap(
                Function.identity(),
                c -> pieces.entrySet().stream()
                    .filter(e -> e.getValue().color() == c)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet())
            ));
            cachedOccupation = Optional.of(occupation);
        }
        return cachedOccupation.get();
    }

    public Set<Pos> occupations() {
        return pieces.keySet();
    }

    public Board withHistory(History h) {
        return new Board(pieces, h);
    }

    public Board updateHistory(UnaryOperator<History> f) {
        return new Board(pieces, f.apply(history));
    }

    public long count(Piece p) {
        return pieces.values().stream()
            .filter(e -> e.equals(p))
            .count();
    }

    public long count(Color c) {
        return pieces.values().stream()
            .filter(e -> e.color().equals(c))
            .count();
    }

    public boolean autodraw() {
        return history.positionHashes().size() > 100 || Color.all.stream().allMatch(c -> {
            List<Role> roles = rolesOf(c).stream().filter(r -> !r.equals(KING)).toList();
            if (roles.size() > 1) return false;
            return roles.isEmpty() || roles.get(0).equals(KNIGHT) || roles.get(0).equals(BISHOP);
        });
    }

    public String positionHash() {
        String positionHash = actors().values().stream().map(Actor::hash).collect(Collectors.joining());
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(positionHash.getBytes());
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new ChessException("MD5 algorithm not found", e);
        }

    }

    public String visual() {
        return visualFormat.obj2Str(this);
    }
    @Override
    public String toString() {
        return visual();
    }

    public Board() {
        this.pieces = cachedStandard.pieces;
        this.history = cachedStandard.history;
    }

    public Board(Map<Pos, Piece> pieces) {
        this(pieces, new History());
    }

    private static Board cachedStandard;

    static {
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
                piecesNew.put(Pos.posAt(x, y).get(), piece);
            }
        }
        cachedStandard = new Board(piecesNew, new History());
    }

    public static Board empty() {
        return new Board(Map.of());
    }

}

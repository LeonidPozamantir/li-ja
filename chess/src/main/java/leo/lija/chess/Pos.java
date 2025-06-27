package leo.lija.chess;

import leo.lija.chess.utils.Pair;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pos {

    @EqualsAndHashCode.Include
    @Getter
    private final int x;
    @EqualsAndHashCode.Include
    @Getter
    private final int y;

    private Optional<Optional<Pos>> cachedUp = Optional.empty();
    private Optional<Optional<Pos>> cachedDown = Optional.empty();
    private Optional<Optional<Pos>> cachedLeft = Optional.empty();
    private Optional<Optional<Pos>> cachedRight = Optional.empty();
    private Optional<Optional<Pos>> cachedUpLeft = Optional.empty();
    private Optional<Optional<Pos>> cachedUpRight = Optional.empty();
    private Optional<Optional<Pos>> cachedDownLeft = Optional.empty();
    private Optional<Optional<Pos>> cachedDownRight = Optional.empty();

    private Optional<String> cachedFile = Optional.empty();
    private Optional<String> cachedRank = Optional.empty();
    private Optional<String> cachedKey = Optional.empty();

    public Optional<Pos> up() {
        if (cachedUp.isEmpty()) cachedUp = Optional.of(shiftUp(1));
        return cachedUp.get();
    }
    public Optional<Pos> down() {
        if (cachedDown.isEmpty()) cachedDown = Optional.of(shiftDown(1));
        return cachedDown.get();
    }
    public Optional<Pos> left() {
        if (cachedLeft.isEmpty()) cachedLeft = Optional.of(shiftLeft(1));
        return cachedLeft.get();
    }
    public Optional<Pos> right() {
        if (cachedRight.isEmpty()) cachedRight = Optional.of(shiftRight(1));
        return cachedRight.get();
    }
    public Optional<Pos> upLeft() {
        if (cachedUpLeft.isEmpty()) cachedUpLeft = Optional.of(shiftUp(1).flatMap(p -> p.shiftLeft(1)));
        return cachedUpLeft.get();
    }
    public Optional<Pos> upRight() {
        if (cachedUpRight.isEmpty()) cachedUpRight = Optional.of(shiftUp(1).flatMap(p -> p.shiftRight(1)));
        return cachedUpRight.get();
    }
    public Optional<Pos> downLeft() {
        if (cachedDownLeft.isEmpty()) cachedDownLeft = Optional.of(shiftDown(1).flatMap(p -> p.shiftLeft(1)));
        return cachedDownLeft.get();
    }
    public Optional<Pos> downRight() {
        if (cachedDownRight.isEmpty()) cachedDownRight = Optional.of(shiftDown(1).flatMap(p -> p.shiftRight(1)));
        return cachedDownRight.get();
    }

    public Optional<Pos> shiftUp(int n) {
        return makePos(x, y + n);
    }

    public Optional<Pos> shiftDown(int n) {
        return makePos(x, y - n);
    }

    public Optional<Pos> shiftLeft(int n) {
        return makePos(x - n, y);
    }

    public Optional<Pos> shiftRight(int n) {
        return makePos(x + n, y);
    }

    public List<Pos> multShiftLeft(Predicate<Pos> stop) {
        return multShift(stop, Pos::left);
    }

    public List<Pos> multShiftRight(Predicate<Pos> stop) {
        return multShift(stop, Pos::right);
    }

    public List<Pos> multShift(Predicate<Pos> stop, Function<Pos, Optional<Pos>> dir) {
        return dir.apply(this)
            .map(p -> {
                LinkedList<Pos> res =  stop.test(p) ? new LinkedList<>() : (LinkedList<Pos>) p.multShift(stop, dir);
                res.addFirst(p);
                return res;
            })
            .orElse(new LinkedList<>());
    }

    public boolean toLeft(Pos other) {
        return x < other.x;
    }

    public boolean toRight(Pos other) {
        return x > other.x;
    }

    public boolean isVertical(Pos other) {
        return x == other.x;
    }

    public List<Pos> horizontalPath(Pos other) {
        return IntStream.range(Math.min(x, other.x), Math.max(x, other.x) + 1)
            .mapToObj(i -> makePos(i, y))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    public String file() {
        if (cachedFile.isEmpty()) cachedFile = Optional.of(xToString(x));
        return cachedFile.get();
    }

    public String rank() {
        if (cachedRank.isEmpty()) cachedRank = Optional.of(String.valueOf(y));
        return cachedRank.get();
    }

    public String key() {
        if (cachedKey.isEmpty()) cachedKey = Optional.of(file() + rank());
        return cachedKey.get();
    }

    @Override
    public String toString() {
        return (char) (x + 96) + String.valueOf(y);
    }

    public static Optional<Pos> makePos(int x, int y) {
        return Optional.ofNullable(allCoords.get(Pair.of(x, y)));
    }

    public static Pos atUnsafe(int x, int y) {
        return allCoords.get(Pair.of(x, y));
    }

    private static String xToString(int x) {
        return String.valueOf((char) (x + 96));
    }

    public static final Pos A1 = new Pos(1, 1);
    public static final Pos A2 = new Pos(1, 2);
    public static final Pos A3 = new Pos(1, 3);
    public static final Pos A4 = new Pos(1, 4);
    public static final Pos A5 = new Pos(1, 5);
    public static final Pos A6 = new Pos(1, 6);
    public static final Pos A7 = new Pos(1, 7);
    public static final Pos A8 = new Pos(1, 8);
    public static final Pos B1 = new Pos(2, 1);
    public static final Pos B2 = new Pos(2, 2);
    public static final Pos B3 = new Pos(2, 3);
    public static final Pos B4 = new Pos(2, 4);
    public static final Pos B5 = new Pos(2, 5);
    public static final Pos B6 = new Pos(2, 6);
    public static final Pos B7 = new Pos(2, 7);
    public static final Pos B8 = new Pos(2, 8);
    public static final Pos C1 = new Pos(3, 1);
    public static final Pos C2 = new Pos(3, 2);
    public static final Pos C3 = new Pos(3, 3);
    public static final Pos C4 = new Pos(3, 4);
    public static final Pos C5 = new Pos(3, 5);
    public static final Pos C6 = new Pos(3, 6);
    public static final Pos C7 = new Pos(3, 7);
    public static final Pos C8 = new Pos(3, 8);
    public static final Pos D1 = new Pos(4, 1);
    public static final Pos D2 = new Pos(4, 2);
    public static final Pos D3 = new Pos(4, 3);
    public static final Pos D4 = new Pos(4, 4);
    public static final Pos D5 = new Pos(4, 5);
    public static final Pos D6 = new Pos(4, 6);
    public static final Pos D7 = new Pos(4, 7);
    public static final Pos D8 = new Pos(4, 8);
    public static final Pos E1 = new Pos(5, 1);
    public static final Pos E2 = new Pos(5, 2);
    public static final Pos E3 = new Pos(5, 3);
    public static final Pos E4 = new Pos(5, 4);
    public static final Pos E5 = new Pos(5, 5);
    public static final Pos E6 = new Pos(5, 6);
    public static final Pos E7 = new Pos(5, 7);
    public static final Pos E8 = new Pos(5, 8);
    public static final Pos F1 = new Pos(6, 1);
    public static final Pos F2 = new Pos(6, 2);
    public static final Pos F3 = new Pos(6, 3);
    public static final Pos F4 = new Pos(6, 4);
    public static final Pos F5 = new Pos(6, 5);
    public static final Pos F6 = new Pos(6, 6);
    public static final Pos F7 = new Pos(6, 7);
    public static final Pos F8 = new Pos(6, 8);
    public static final Pos G1 = new Pos(7, 1);
    public static final Pos G2 = new Pos(7, 2);
    public static final Pos G3 = new Pos(7, 3);
    public static final Pos G4 = new Pos(7, 4);
    public static final Pos G5 = new Pos(7, 5);
    public static final Pos G6 = new Pos(7, 6);
    public static final Pos G7 = new Pos(7, 7);
    public static final Pos G8 = new Pos(7, 8);
    public static final Pos H1 = new Pos(8, 1);
    public static final Pos H2 = new Pos(8, 2);
    public static final Pos H3 = new Pos(8, 3);
    public static final Pos H4 = new Pos(8, 4);
    public static final Pos H5 = new Pos(8, 5);
    public static final Pos H6 = new Pos(8, 6);
    public static final Pos H7 = new Pos(8, 7);
    public static final Pos H8 = new Pos(8, 8);

    private static final Map<String, Pos> allKeys = Map.ofEntries(
            Map.entry("a1", A1), Map.entry("a2", A2), Map.entry("a3", A3), Map.entry("a4", A4), Map.entry("a5", A5), Map.entry("a6", A6), Map.entry("a7", A7), Map.entry("a8", A8),
            Map.entry("b1", B1), Map.entry("b2", B2), Map.entry("b3", B3), Map.entry("b4", B4), Map.entry("b5", B5), Map.entry("b6", B6), Map.entry("b7", B7), Map.entry("b8", B8),
            Map.entry("c1", C1), Map.entry("c2", C2), Map.entry("c3", C3), Map.entry("c4", C4), Map.entry("c5", C5), Map.entry("c6", C6), Map.entry("c7", C7), Map.entry("c8", C8),
            Map.entry("d1", D1), Map.entry("d2", D2), Map.entry("d3", D3), Map.entry("d4", D4), Map.entry("d5", D5), Map.entry("d6", D6), Map.entry("d7", D7), Map.entry("d8", D8),
            Map.entry("e1", E1), Map.entry("e2", E2), Map.entry("e3", E3), Map.entry("e4", E4), Map.entry("e5", E5), Map.entry("e6", E6), Map.entry("e7", E7), Map.entry("e8", E8),
            Map.entry("f1", F1), Map.entry("f2", F2), Map.entry("f3", F3), Map.entry("f4", F4), Map.entry("f5", F5), Map.entry("f6", F6), Map.entry("f7", F7), Map.entry("f8", F8),
            Map.entry("g1", G1), Map.entry("g2", G2), Map.entry("g3", G3), Map.entry("g4", G4), Map.entry("g5", G5), Map.entry("g6", G6), Map.entry("g7", G7), Map.entry("g8", G8),
            Map.entry("h1", H1), Map.entry("h2", H2), Map.entry("h3", H3), Map.entry("h4", H4), Map.entry("h5", H5), Map.entry("h6", H6), Map.entry("h7", H7), Map.entry("h8", H8)
    );

    private static final Map<Pair<Integer, Integer>, Pos> allCoords = IntStream.rangeClosed(1, 8)
        .boxed()
        .flatMap(x -> IntStream.rangeClosed(1, 8)
            .mapToObj(y -> {
                String key = xToString(x) + y;
                return Pair.of(Pair.of(x, y), allKeys.get(key));
            }))
        .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

    public static Collection<Pos> all() {
        return allKeys.values();
    }
}

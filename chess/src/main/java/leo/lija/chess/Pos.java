package leo.lija.chess;

import leo.lija.chess.utils.Pair;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    @Getter
    private final char piotr;

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
        if (cachedUp.isEmpty()) cachedUp = Optional.of(posAt(x, y + 1));
        return cachedUp.get();
    }
    public Optional<Pos> down() {
        if (cachedDown.isEmpty()) cachedDown = Optional.of(posAt(x, y - 1));
        return cachedDown.get();
    }
    public Optional<Pos> left() {
        if (cachedLeft.isEmpty()) cachedLeft = Optional.of(posAt(x - 1, y));
        return cachedLeft.get();
    }
    public Optional<Pos> right() {
        if (cachedRight.isEmpty()) cachedRight = Optional.of(posAt(x + 1, y));
        return cachedRight.get();
    }
    public Optional<Pos> upLeft() {
        if (cachedUpLeft.isEmpty()) cachedUpLeft = Optional.of(up().flatMap(Pos::left));
        return cachedUpLeft.get();
    }
    public Optional<Pos> upRight() {
        if (cachedUpRight.isEmpty()) cachedUpRight = Optional.of(up().flatMap(Pos::right));
        return cachedUpRight.get();
    }
    public Optional<Pos> downLeft() {
        if (cachedDownLeft.isEmpty()) cachedDownLeft = Optional.of(down().flatMap(Pos::left));
        return cachedDownLeft.get();
    }
    public Optional<Pos> downRight() {
        if (cachedDownRight.isEmpty()) cachedDownRight = Optional.of(down().flatMap(Pos::right));
        return cachedDownRight.get();
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
            .mapToObj(i -> posAt(i, y))
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

    public static Optional<Pos> posAt(int x, int y) {
        return Optional.ofNullable(allCoords.get(Pair.of(x, y)));
    }

    public static Optional<Pos> posAt(String key) {
        return Optional.ofNullable(allKeys.get(key));
    }

    private static String xToString(int x) {
        return String.valueOf((char) (x + 96));
    }

    public static Optional<Pos> piotr(Character c) {
        return Optional.ofNullable(allPiotrs.get(c));
    }

    public static final Pos A1 = new Pos(1, 1, 'a');
    public static final Pos B1 = new Pos(2, 1, 'b');
    public static final Pos C1 = new Pos(3, 1, 'c');
    public static final Pos D1 = new Pos(4, 1, 'd');
    public static final Pos E1 = new Pos(5, 1, 'e');
    public static final Pos F1 = new Pos(6, 1, 'f');
    public static final Pos G1 = new Pos(7, 1, 'g');
    public static final Pos H1 = new Pos(8, 1, 'h');
    public static final Pos A2 = new Pos(1, 2, 'i');
    public static final Pos B2 = new Pos(2, 2, 'j');
    public static final Pos C2 = new Pos(3, 2, 'k');
    public static final Pos D2 = new Pos(4, 2, 'l');
    public static final Pos E2 = new Pos(5, 2, 'm');
    public static final Pos F2 = new Pos(6, 2, 'n');
    public static final Pos G2 = new Pos(7, 2, 'o');
    public static final Pos H2 = new Pos(8, 2, 'p');
    public static final Pos A3 = new Pos(1, 3, 'q');
    public static final Pos B3 = new Pos(2, 3, 'r');
    public static final Pos C3 = new Pos(3, 3, 's');
    public static final Pos D3 = new Pos(4, 3, 't');
    public static final Pos E3 = new Pos(5, 3, 'u');
    public static final Pos F3 = new Pos(6, 3, 'v');
    public static final Pos G3 = new Pos(7, 3, 'w');
    public static final Pos H3 = new Pos(8, 3, 'x');
    public static final Pos A4 = new Pos(1, 4, 'y');
    public static final Pos B4 = new Pos(2, 4, 'z');
    public static final Pos C4 = new Pos(3, 4, 'A');
    public static final Pos D4 = new Pos(4, 4, 'B');
    public static final Pos E4 = new Pos(5, 4, 'C');
    public static final Pos F4 = new Pos(6, 4, 'D');
    public static final Pos G4 = new Pos(7, 4, 'E');
    public static final Pos H4 = new Pos(8, 4, 'F');
    public static final Pos A5 = new Pos(1, 5, 'G');
    public static final Pos B5 = new Pos(2, 5, 'H');
    public static final Pos C5 = new Pos(3, 5, 'I');
    public static final Pos D5 = new Pos(4, 5, 'J');
    public static final Pos E5 = new Pos(5, 5, 'K');
    public static final Pos F5 = new Pos(6, 5, 'L');
    public static final Pos G5 = new Pos(7, 5, 'M');
    public static final Pos H5 = new Pos(8, 5, 'N');
    public static final Pos A6 = new Pos(1, 6, 'O');
    public static final Pos B6 = new Pos(2, 6, 'P');
    public static final Pos C6 = new Pos(3, 6, 'Q');
    public static final Pos D6 = new Pos(4, 6, 'R');
    public static final Pos E6 = new Pos(5, 6, 'S');
    public static final Pos F6 = new Pos(6, 6, 'T');
    public static final Pos G6 = new Pos(7, 6, 'U');
    public static final Pos H6 = new Pos(8, 6, 'V');
    public static final Pos A7 = new Pos(1, 7, 'W');
    public static final Pos B7 = new Pos(2, 7, 'X');
    public static final Pos C7 = new Pos(3, 7, 'Y');
    public static final Pos D7 = new Pos(4, 7, 'Z');
    public static final Pos E7 = new Pos(5, 7, '0');
    public static final Pos F7 = new Pos(6, 7, '1');
    public static final Pos G7 = new Pos(7, 7, '2');
    public static final Pos H7 = new Pos(8, 7, '3');
    public static final Pos A8 = new Pos(1, 8, '4');
    public static final Pos B8 = new Pos(2, 8, '5');
    public static final Pos C8 = new Pos(3, 8, '6');
    public static final Pos D8 = new Pos(4, 8, '7');
    public static final Pos E8 = new Pos(5, 8, '8');
    public static final Pos F8 = new Pos(6, 8, '9');
    public static final Pos G8 = new Pos(7, 8, '!');
    public static final Pos H8 = new Pos(8, 8, '?');

    private static List<Pos> all = List.of(A1, B1, C1, D1, E1, F1, G1, H1, A2, B2, C2, D2, E2, F2, G2, H2, A3, B3, C3, D3, E3, F3, G3, H3, A4, B4, C4, D4, E4, F4, G4, H4, A5, B5, C5, D5, E5, F5, G5, H5, A6, B6, C6, D6, E6, F6, G6, H6, A7, B7, C7, D7, E7, F7, G7, H7, A8, B8, C8, D8, E8, F8, G8, H8);

    private static final Map<String, Pos> allKeys = all.stream().collect(Collectors.toMap(Pos::key, Function.identity()));

    private static final Map<Character, Pos> allPiotrs = all.stream().collect(Collectors.toMap(pos -> pos.piotr, Function.identity()));

    private static final Map<Pair<Integer, Integer>, Pos> allCoords = all.stream().collect(Collectors.toMap(p -> Pair.of(p.x, p.y), Function.identity()));
}

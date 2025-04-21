package leo.lija.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
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

    private Optional<Optional<Pos>> optUp = Optional.empty();
    private Optional<Optional<Pos>> optDown = Optional.empty();
    private Optional<Optional<Pos>> optLeft = Optional.empty();
    private Optional<Optional<Pos>> optRight = Optional.empty();
    private Optional<Optional<Pos>> optUpLeft = Optional.empty();
    private Optional<Optional<Pos>> optUpRight = Optional.empty();
    private Optional<Optional<Pos>> optDownLeft = Optional.empty();
    private Optional<Optional<Pos>> optDownRight = Optional.empty();

    public Optional<Pos> up() {
        if (optUp.isEmpty()) optUp = Optional.of(shiftUp(1));
        return optUp.get();
    }
    public Optional<Pos> down() {
        if (optDown.isEmpty()) optDown = Optional.of(shiftDown(1));
        return optDown.get();
    }
    public Optional<Pos> left() {
        if (optLeft.isEmpty()) optLeft = Optional.of(shiftLeft(1));
        return optLeft.get();
    }
    public Optional<Pos> right() {
        if (optRight.isEmpty()) optRight = Optional.of(shiftRight(1));
        return optRight.get();
    }
    public Optional<Pos> upLeft() {
        if (optUpLeft.isEmpty()) optUpLeft = Optional.of(shiftUp(1).flatMap(p -> p.shiftLeft(1)));
        return optUpLeft.get();
    }
    public Optional<Pos> upRight() {
        if (optUpRight.isEmpty()) optUpRight = Optional.of(shiftUp(1).flatMap(p -> p.shiftRight(1)));
        return optUpRight.get();
    }
    public Optional<Pos> downLeft() {
        if (optDownLeft.isEmpty()) optDownLeft = Optional.of(shiftDown(1).flatMap(p -> p.shiftLeft(1)));
        return optDownLeft.get();
    }
    public Optional<Pos> downRight() {
        if (optDownRight.isEmpty()) optDownRight = Optional.of(shiftDown(1).flatMap(p -> p.shiftRight(1)));
        return optDownRight.get();
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

    public String xToString() {
        return String.valueOf((char) (x + 96));
    }

    public String yToString() {
        return String.valueOf(y);
    }

    @Override
    public String toString() {
        return xToString() + yToString();
    }

    private static final Set<Integer> bounds = IntStream.rangeClosed(1, 8)
            .boxed()
            .collect(Collectors.toSet());

    public static Optional<Pos> makePos(int x, int y) {
        if (bounds.contains(x) && bounds.contains(y)) return Optional.of(new Pos(x, y)); else return Optional.empty();
    }

    public static Pos atUnsafe(int x, int y) {
        return new Pos(x, y);
    }

    public static Optional<Pos> shiftUp(Optional<Pos> op) {
        return op.flatMap(p -> p.shiftUp(1));
    }

    public static Optional<Pos> shiftDown(Optional<Pos> op) {
        return op.flatMap(p -> p.shiftDown(1));
    }

    public static Optional<Pos> shiftLeft(Optional<Pos> op) {
        return op.flatMap(p -> p.shiftLeft(1));
    }

    public static Optional<Pos> shiftRight(Optional<Pos> op) {
        return op.flatMap(p -> p.shiftRight(1));
    }

    public static List<List<Pos>> vectorBasedPoss(Pos from, List<List<UnaryOperator<Optional<Pos>>>> directions) {
        return directions.stream()
                .map(d -> expand(from, d))
                .filter(exp -> !exp.isEmpty())
                .toList();
    }

    public static List<Pos> expand(Pos from, List<UnaryOperator<Optional<Pos>>> direction) {
        Optional<Pos> candidate = direction.stream().reduce(Optional.of(from), (op, f) -> f.apply(op), (a, b) -> a);
        if (candidate.isPresent()) {
            List<Pos> expanded = expand(candidate.get(), direction);
            expanded.add(candidate.get());
            return expanded.reversed();
        }
        return List.of();
    }

    public static List<Pos> radialBasedPoss(Pos from, Iterable<Integer> offsets, BiPredicate<Integer, Integer> filter) {
        List<Pos> result = List.of();
        for (int y: offsets) {
            for (int x: offsets) {
                if (filter.test(y, x)) {
                    Optional<Pos> candidate = from.shiftUp(y).flatMap(p -> p.shiftRight(x));
                    candidate.ifPresent(result::add);
                }
            }
        }
        return result;
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

    public static Collection<Pos> all() {
        return allKeys.values();
    }
}

package leo.lija.model;

import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@EqualsAndHashCode
public class Pos {

    private final int x;
    private final int y;
    private static final Set<Integer> values = IntStream.rangeClosed(1, 8)
            .boxed()
            .collect(Collectors.toSet());

    private static final Map<String, Pos> allKeys;
    static {
        allKeys = new HashMap<>();
        for (int i: values) {
            for (int j: values) {
                allKeys.put(i + "" + j, new Pos(i, j));
            }
        }
    }


    public Pos(int x, int y) {
        if (!values.contains(x) || !values.contains(y)) {
            throw new IllegalArgumentException("Invalid position");
        }
        this.x = x;
        this.y = y;
    }
    public static Pos at(String s) {
        char c1 = s.charAt(0);
        char c2 = s.charAt(1);
        if (!Character.isDigit(c2) || !Character.isLetter(c1)) {
            throw new IllegalArgumentException("Invalid position");
        }
        return allKeys.get(c1 - 'a' + 1 + "" + c2);
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

    public Optional<Pos> shiftUp(int n) {
        return values.contains(y + n) ? Optional.of(new Pos(x, y + n)) : Optional.empty();
    }

    public Optional<Pos> shiftDown(int n) {
        return values.contains(y - n) ? Optional.of(new Pos(x, y - n)) : Optional.empty();
    }

    public Optional<Pos> shiftLeft(int n) {
        return values.contains(x - n) ? Optional.of(new Pos(x - n, y)) : Optional.empty();
    }

    public Optional<Pos> shiftRight(int n) {
        return values.contains(x + n) ? Optional.of(new Pos(x + n, y)) : Optional.empty();
    }

    public boolean isHoriz(Pos other) {
        return y == other.y;
    }

    public boolean isVert(Pos other) {
        return x == other.x;
    }

    public boolean isDiag(Pos other) {
        return Math.abs(x - other.x) == Math.abs(y - other.y);
    }

    public boolean isLinear(Pos other) {
        return isHoriz(other) || isVert(other) || isDiag(other);
    }

    public List<Pos> multShiftUp(int n) {
        return multShift(n, Pos::shiftUp);
    }

    public List<Pos> multShiftDown(int n) {
        return multShift(n, Pos::shiftDown);
    }

    public List<Pos> multShiftLeft(int n) {
        return multShift(n, Pos::shiftLeft);
    }

    public List<Pos> multShiftRight(int n) {
        return multShift(n, Pos::shiftRight);
    }

    public List<Pos> multShift(int n, UnaryOperator<Optional<Pos>> dir) {
        return expandN(n, Optional.of(this), dir)
                .stream()
                .map(Optional::get)
                .toList();
    }

    public String xToString() {
        return String.valueOf(x);
    }

    public String yToString() {
        return String.valueOf(y);
    }

    @Override
    public String toString() {
        return xToString() + yToString();
    }

    private List<Optional<Pos>> expandN(int n, Optional<Pos> op, UnaryOperator<Optional<Pos>> dir) {
        List<Optional<Pos>> result = List.of();
        Optional<Pos> cur = op;
        while (n >= 0 && cur.isPresent()) {
            result.add(cur);
            cur = dir.apply(cur);
            n--;
        }
        return result;
    }

}

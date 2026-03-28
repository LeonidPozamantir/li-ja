package leo.lija.chess;

import leo.lija.chess.utils.CsvLoader;
import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Eco {

    public record Branch(Map<String, Branch> moves, Optional<Opening> opening) {

        public Branch() {
            this(new HashMap<>(), Optional.empty());
        }

        public Optional<Branch> get(String move) {
            return Optional.ofNullable(moves.get(move));
        }

        public Branch apply(String move) {
            return moves.getOrDefault(move, new Branch());
        }

        public Branch add(List<String> newMoves, Opening opening) {
            if (newMoves.isEmpty()) return this;
            String move = newMoves.getFirst();
            if (newMoves.size() == 1) return this.updated(move, apply(move).set(opening));
            List<String> others = newMoves.subList(1, newMoves.size());
            return this.updated(move, apply(move).add(others, opening));
        }

        public Branch updated(String k, Branch v) {
            moves.put(k, v);
            return this;
        }

        public Branch set(Opening o) {
            return new Branch(moves, Optional.of(o));
        }

        @Override
        public String toString() {
            return opening.map(Opening::name).orElse("-");
        }

        public String render() {
            return render("");
        }

        public String render(String margin) {
            return margin + toString() + "\n" + moves.entrySet().stream()
                .map(e -> margin + e.getKey() + e.getValue().render(margin + "  "))
                .collect(Collectors.joining("\n"));
        }
    }

    public static Optional<Opening> openingOf(String pgn) {
        return next(tree, List.of(pgn.split(" "))).opening;
    }

    private static Branch next(Branch branch, List<String> moves) {
        if (moves.isEmpty()) return branch;
        String m = moves.getFirst();
        List<String> ms = moves.subList(1, moves.size());
        return branch.get(m)
            .map(b -> next(b, ms))
            .orElse(branch);
    }

    @Getter
    static Branch tree;

    static {
        try {
            tree = CsvLoader.load("eco.csv").stream()
                .reduce(new Branch(),
                    (acc, t3) -> acc.add(List.of(t3.get(2).split(" ")), new Opening(t3.get(0), t3.get(1))),
                    (b1, b2) -> b1
                );
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}

package leo.lija.chess;

import leo.lija.chess.utils.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class Eco {

    record Branch(Map<String, Branch> moves, Optional<String> name) {

        public Branch() {
            this(new HashMap<>(), Optional.empty());
        }

        public Optional<Branch> get(String move) {
            return Optional.ofNullable(moves.get(move));
        }

        public Branch apply(String move) {
            return moves.getOrDefault(move, new Branch());
        }

        public Branch add(List<String> newMoves, String cname) {
            if (newMoves.isEmpty()) return this;
            String move = newMoves.getFirst();
            if (newMoves.size() == 1) return this.updated(move, apply(move).withName(cname));
            List<String> others = newMoves.subList(1, newMoves.size());
            return this.updated(move, apply(move).add(others, cname));
        }

        public Branch updated(String k, Branch v) {
            moves.put(k, v);
            return this;
        }

        public Branch withName(String name) {
            return new Branch(moves, Optional.of(name));
        }
    }

    public static Optional<String> nameOf(String pgn) {
        return Arrays.stream(pgn.split(" ")).reduce(tree,
            (branch, move) -> branch.get(move).orElse(branch),
            (b1, b2) -> b1
        ).name;
    }

    static Branch tree = Stream.<Pair<String, String>>of(
        Pair.of("b3", "Nimzovich-Larsen Attack"),
        Pair.of("f4", "Bird's Opening"),
        Pair.of("f4 d5", "Bird's Opening"),
        Pair.of("Nf3", "Reti Opening"),
        Pair.of("Nf3 Nf6", "Reti Opening"),
        Pair.of("Nf3 d5", "Reti Opening"),
        Pair.of("Nf3 d5 g3", "King's Indian Attack"),
        Pair.of("Nf3 d5 g3 c5 Bg2", "King's Indian Attack"),
        Pair.of("Nf3 d5 c4", "Reti Opening"),

        Pair.of("c4", "English"),
        Pair.of("c4 c6", "English, Caro-Kann Defensive System"),
        Pair.of("c4 c6 Nf3 d5 b3", "English with b3"),
        Pair.of("c4 e6", "English"),
        Pair.of("c4 e6 Nf3 d5 g3 Nf6 Bg2 Be7 O-O", "English"),
        Pair.of("c4 Nf6", "English"),
        Pair.of("c4 Nf6 Nc3", "English"),
        Pair.of("c4 Nf6 Nc3 e6", "English"),
        Pair.of("c4 Nf6 Nc3 e6 e4", "English, Mikenas-Carls"),
        Pair.of("c4 Nf6 Nc3 e6 e4 c5", "English, Mikenas-Carls, Sicilian Variation"),

        Pair.of("c4 e5", "English"),
        Pair.of("c4 e5 Nc3", "English"),
        Pair.of("c4 e5 Nc3 Nf6", "English"),
        Pair.of("c4 e5 Nc3 Nf6 g3 c6", "English, Bremen System, Keres Variation"),
        Pair.of("c4 e5 Nc3 Nf6 g3 g6", "English, Bremen System with ...g6"),

        Pair.of("c4 c5", "English, Symmetrical"),
        Pair.of("c4 c5 Nf3 Nf6 d4", "English, Symmetrical, Benoni Formation"),
        Pair.of("c4 c5 Nf3 Nf6 d4 cxd4 Nxd4 e6", "English, Symmetrical Variation"),

        Pair.of("d4", "Queen's Pawn Game"),
        Pair.of("d4 d6", "Queen's Pawn Game (with ...d6)"),
        Pair.of("d4 d6 c4 g6 Nc3 Bg7 e4", "Modern Defense, Averbakh System"),

        Pair.of("d4 c5", "Old Benoni"),
        Pair.of("d4 c5 d5 e5", "Old Benoni Defense"),

        Pair.of("d4 Nf6", "Queen's Pawn Game"),
        Pair.of("d4 Nf6 Nf3", "Queen's Pawn Game"),
        Pair.of("d4 Nf6 Nf3 b6", "Queen's Indian"),
        Pair.of("d4 Nf6 Nf3 g6", "King's Indian"),
        Pair.of("d4 Nf6 Nf3 g6 g3", "King's Indian, Fianchetto without c4"),

        Pair.of("e4", "Uncommon King's Pawn Opening"),
        Pair.of("e4 d5", "Scandinavian"),
        Pair.of("e4 Nf6", "Alekhine's Defense"),

        Pair.of("e4 c6", "Caro-Kann"),
        Pair.of("e4 c6 d4", "Caro-Kann Defense"),
        Pair.of("e4 c6 d4 d5 exd5 cxd5", "Caro-Kann, Exchange"),

        Pair.of("e4 c5", "Sicilian"),
        Pair.of("e4 c5 c3", "Sicilian, Alapin"),
        Pair.of("e4 c5 Nc3", "Sicilian, Closed"),
        Pair.of("e4 c5 Nf3", "Sicilian"),

        Pair.of("e4 e6", "French Defense"),
        Pair.of("e4 e6 d4 d5 e5", "French, Advance"),
        Pair.of("e4 e6 d4 d5 Nd2", "French, Tarrasch"),

        Pair.of("e4 e5", "King's Pawn Game"),
        Pair.of("e4 e5 d4 exd4", "Center Game"),
        Pair.of("e4 e5 Bc4", "Bishop's Opening"),
        Pair.of("e4 e5 Nc3", "Vienna"),
        Pair.of("e4 e5 f4", "King's Gambit Declined"),

        Pair.of("e4 e5 Nf3", "King's Knight Opening"),
        Pair.of("e4 e5 Nf3 d6", "Philidor Defense"),
        Pair.of("e4 e5 Nf3 Nf6", "Petrov Defense"),
        Pair.of("e4 e5 Nf3 Nc6", "King's Pawn Game"),

        Pair.of("e4 e5 Nf3 Nc6 Bb5", "Ruy Lopez")
    ).reduce(new Branch(),
        (acc, p) -> acc.add(List.of(p.getFirst().split(" ")), p.getSecond()),
        (b1, b2) -> b1
    );
}

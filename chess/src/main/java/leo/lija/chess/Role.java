package leo.lija.chess;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Role {
    KING('k',false, List.of(Pos::up, Pos::down, Pos::left, Pos::right, Pos::upLeft, Pos::upRight, Pos::downLeft, Pos::downRight)),
    QUEEN('q',true, List.of(Pos::up, Pos::down, Pos::left, Pos::right, Pos::upLeft, Pos::upRight, Pos::downLeft, Pos::downRight)),
    ROOK('r', true, List.of(Pos::up, Pos::down, Pos::left, Pos::right)),
    BISHOP('b', true, List.of(Pos::upLeft, Pos::upRight, Pos::downLeft, Pos::downRight)),
    KNIGHT('n', true, List.of(
        pos -> pos.up().flatMap(Pos::upLeft),
        pos -> pos.up().flatMap(Pos::upRight),
        pos -> pos.left().flatMap(Pos::upLeft),
        pos -> pos.left().flatMap(Pos::downLeft),
        pos -> pos.right().flatMap(Pos::upRight),
        pos -> pos.right().flatMap(Pos::downRight),
        pos -> pos.down().flatMap(Pos::downLeft),
        pos -> pos.down().flatMap(Pos::downRight)
    )),
    PAWN('p', false,null);

    public final char fen;
    public final boolean isPromotable;
    final List<Function<Pos, Optional<Pos>>> dirs;

    private Optional<Character> cachedPgn = Optional.empty();
    public char pgn() {
        if (cachedPgn.isEmpty()) cachedPgn = Optional.of((char) (fen - 32));
        return cachedPgn.get();
    }

    public static final List<Role> all = List.of(KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN);
    private static final Map<Character, Role> allByFen = all.stream().collect(Collectors.toMap(r -> r.fen, Function.identity()));
    public static final List<Role> allPromotable = List.of(QUEEN, ROOK, BISHOP, KNIGHT);
    public static final Map<String, Role> allPromotableByName = Collections.unmodifiableMap(allPromotable.stream().collect(Collectors.toMap(Role::toString, Function.identity())));
    public static final Map<Character, Role> allPromotableByFen = Collections.unmodifiableMap(allPromotable.stream().collect(Collectors.toMap(r -> r.fen, Function.identity())));

    public static Optional<Role> fen(Character c) {
        return Optional.ofNullable(allByFen.get(c));
    }

    public static Optional<Role> promotable(Character c) {
        return Optional.ofNullable(allPromotableByFen.get(c));
    }

    public static Optional<Role> promotable(String name) {
        return Optional.ofNullable(allPromotableByName.get(name));
    }

    public static Optional<Role> promotable(Optional<String> name) {
        return name.flatMap(n -> promotable(n));//.orElse(Optional.of(QUEEN)); - incorrect, fill be fixed 11/05
    }
}

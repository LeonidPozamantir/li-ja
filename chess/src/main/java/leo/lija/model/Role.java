package leo.lija.model;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public enum Role {
    KING('k',false, List.of(Pos::up, Pos::down, Pos::left, Pos::right, Pos::upLeft, Pos::upRight, Pos::downLeft, Pos::downRight)),
    QUEEN('q',true, List.of(Pos::up, Pos::down, Pos::left, Pos::right, Pos::upLeft, Pos::upRight, Pos::downLeft, Pos::downRight)),
    ROOK('r', true, List.of(Pos::up, Pos::down, Pos::left, Pos::right)),
    BISHOP('b', true, List.of(Pos::upLeft, Pos::upRight, Pos::downLeft, Pos::downRight)),
    KNIGHT('n', false, List.of(
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

    public static final List<Role> all = List.of(KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN);

    public final char fen;
    public final boolean longRange;
    final List<Function<Pos, Optional<Pos>>> dirs;

}

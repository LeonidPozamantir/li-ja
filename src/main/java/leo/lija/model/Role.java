package leo.lija.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public enum Role {
    KING('k',false,null),
    QUEEN('q',true, List.of(Pos::up, Pos::down, Pos::left, Pos::right, Pos::upLeft, Pos::upRight, Pos::downLeft, Pos::downRight)),
    ROOK('r', true, List.of(Pos::up, Pos::down, Pos::left, Pos::right)),
    BISHOP('b', true, List.of(Pos::upLeft, Pos::upRight, Pos::downLeft, Pos::downRight)),
    KNIGHT('n', false,null),
    PAWN('p', false,null);

    public static final List<Role> all = List.of(KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN);

    public final char fen;
    public final boolean directed;
    final List<Function<Pos, Optional<Pos>>> dirs;

}

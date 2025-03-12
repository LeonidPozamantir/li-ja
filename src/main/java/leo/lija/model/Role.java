package leo.lija.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public enum Role {
    KING('k', null),
    QUEEN('q', null),
    ROOK('r', List.of(Pos::up, Pos::down, Pos::left, Pos::right)),
    BISHOP('b', null),
    KNIGHT('n', null),
    PAWN('p', null);

    public static final List<Role> all = List.of(KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN);

    public final char fen;
    public final List<Function<Pos, Optional<Pos>>> dirs;

}

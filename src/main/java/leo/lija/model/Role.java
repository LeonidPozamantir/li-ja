package leo.lija.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public enum Role {
    KING('k'),
    QUEEN('q'),
    ROOK('r'),
    BISHOP('b'),
    KNIGHT('n'),
    PAWN('p');

    public static final List<Role> all = List.of(KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN);

    public final char fen;

}

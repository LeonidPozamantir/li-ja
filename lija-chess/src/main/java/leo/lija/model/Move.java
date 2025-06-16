package leo.lija.model;

import leo.lija.exceptions.ChessRulesException;

import java.util.Optional;

public record Move(
    Piece piece,
    Pos orig,
    Pos dest,
    Board before,
    Board after,
    Optional<Pos> capture,
    Optional<Role> promotion,
    boolean castle,
    boolean enpassant
) {

    public Move {
        if (promotion.isPresent() && !promotion.get().promotable) {
            throw new ChessRulesException("Can't promote to %s".formatted(promotion));
        }
    }

    public Move withHistory(History h) {
        return new Move(piece, orig, dest, before, after.withHistory(h), capture, promotion, castle, enpassant);
    }
}

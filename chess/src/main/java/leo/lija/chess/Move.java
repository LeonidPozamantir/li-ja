package leo.lija.chess;

import io.vavr.collection.List;
import leo.lija.chess.exceptions.ChessRulesException;
import leo.lija.chess.utils.Pair;
import lombok.With;

import java.util.Optional;

import static leo.lija.chess.Role.PAWN;

public record Move(
    Piece piece,
    Pos orig,
    Pos dest,
    Board before,
    @With Board after,
    Optional<Pos> capture,
    @With Optional<Role> promotion,
    Optional<Pair<Pair<Pos, Pos>, Pair<Pos, Pos>>> castle,
    boolean enpassant
) {

    public Move {
        if (promotion.isPresent() && !promotion.get().isPromotable) {
            throw new ChessRulesException("Can't promote to %s".formatted(promotion));
        }
    }

    public Move withHistory(History h) {
        return new Move(piece, orig, dest, before, after.withHistory(h), capture, promotion, castle, enpassant);
    }

    public Board finalizeAfter() {
        return after.updateHistory(h -> {
            List<String> positionHashes = piece.is(PAWN) || captures() || promotes() || castles()
                ? List.empty()
                : h.positionHashesWith(after.positionHash());
            return new History(Optional.of(Pair.of(orig, dest)), positionHashes, h.whiteCastleKingSide(), h.whiteCastleQueenSide(), h.blackCastleKingSide(), h.blackCastleQueenSide());
        });
    }

    // does this move capture an opponent piece?
    public boolean captures() {
        return capture.isPresent();
    }

    public boolean promotes() {
        return promotion.isPresent();
    }

    public boolean castles() {
        return castle.isPresent();
    }

    public Color color() {
        return piece.color();
    }

    @Override
    public String toString() {
        return orig + " " + dest;
    }
}

package leo.lija.chess;

import io.vavr.collection.List;
import leo.lija.chess.exceptions.ChessRulesException;
import leo.lija.chess.utils.Pair;
import lombok.Builder;
import lombok.With;

import java.util.Optional;

import static leo.lija.chess.Role.KING;
import static leo.lija.chess.Role.PAWN;
import static leo.lija.chess.Role.ROOK;

@Builder(toBuilder = true)
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
            History h1 = new History(Optional.of(Pair.of(orig, dest)), positionHashes, h.whiteCastleKingSide(), h.whiteCastleQueenSide(), h.blackCastleKingSide(), h.blackCastleQueenSide());
            if (piece.is(KING) && h1.canCastle(color())) return h1.withoutCastles(color());
            if (piece.is(ROOK)) {
                return after.kingPosOf(color())
                    .flatMap(kingPos -> Side.kingRookSide(kingPos, orig))
                    .filter(side -> h1.canCastle(color(), side))
                    .map(side -> h1.withoutCastle(color(), side))
                    .orElse(h1);
            }
            return h1;
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

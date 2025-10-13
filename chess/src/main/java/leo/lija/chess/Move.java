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
        return after.updateHistory(h1 -> {
            // last move and position hashes
            List<String> positionHashes = piece.is(PAWN) || captures() || promotes() || castles()
                ? List.empty()
                : h1.positionHashesWith(after.positionHash());
            History h2 = new History(Optional.of(Pair.of(orig, dest)), positionHashes, h1.whiteCastleKingSide(), h1.whiteCastleQueenSide(), h1.blackCastleKingSide(), h1.blackCastleQueenSide());
            // my broken castles
            History h3;
            if (piece.is(KING) && h2.canCastle(color())) h3 = h2.withoutCastles(color());
            else if (piece.is(ROOK)) {
                h3 = after.kingPosOf(color())
                    .flatMap(kingPos -> Side.kingRookSide(kingPos, orig))
                    .filter(side -> h2.canCastle(color(), side))
                    .map(side -> h2.withoutCastle(color(), side))
                    .orElse(h2);
            } else h3 = h2;
            // opponent broken castles
            return capture
                .flatMap(cPos -> before.at(cPos)
                    .filter(cPiece -> cPiece.is(ROOK))
                    .flatMap(cPiece -> after.kingPosOf(color().getOpposite())
                        .flatMap(kingPos -> Side.kingRookSide(kingPos, cPos)
                            .filter(side -> h3.canCastle(color().getOpposite(), side))
                            .map(side -> h3.withoutCastle(color().getOpposite(), side)))))
                .orElse(h3);
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

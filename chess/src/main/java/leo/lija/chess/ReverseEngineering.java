package leo.lija.chess;


import leo.lija.chess.utils.Pair;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static leo.lija.chess.Role.KING;
import static leo.lija.chess.Role.ROOK;

public class ReverseEngineering {

    private final Game fromGame;
    private final Board to;
    private final Board from;

    public ReverseEngineering(Game fromGame, Board to) {
        this.fromGame = fromGame;
        this.to = to;
        from = fromGame.board;
    }

    public Optional<Pair<Pos, Pos>> move() {
        if (from.getPieces().equals(to.getPieces())) return Optional.empty();
        return findMove();
    }

    private Optional<Pair<Pos, Pos>> findMove() {
        List<Pair<Pos, Piece>> movedPieces = findMovedPieces();
        if (movedPieces.size() == 1) {
            Pos pos = movedPieces.get(0).getFirst();
            Piece piece = movedPieces.get(0).getSecond();
            return findPieceNewPos(pos, piece).map(np -> Pair.of(pos, np));
        }
        if (movedPieces.size() == 2) {
            Pos pos1 = movedPieces.get(0).getFirst();
            Piece piece1 = movedPieces.get(0).getSecond();
            Pos pos2 = movedPieces.get(1).getFirst();
            Piece piece2 = movedPieces.get(1).getSecond();
            if (piece1.is(KING) && piece2.is(ROOK)) return findCastle(pos2).map(np -> Pair.of(pos1, np));
            if (piece1.is(ROOK) && piece2.is(KING)) return findCastle(pos1).map(np -> Pair.of(pos2, np));
        }
        return Optional.empty();
    }

    private Optional<Pos> findCastle(Pos rookPos) {
        return switch (rookPos.getX()) {
            case 1 -> Pos.posAt(3, rookPos.getY());
            case 8 -> Pos.posAt(7, rookPos.getY());
            default -> Optional.empty();
        };
    }

    private Optional<Pos> findPieceNewPos(Pos pos, Piece piece) {
        return Optional.ofNullable(fromGame.situation().destinations().get(pos))
            .flatMap(dests -> dests.stream()
                .filter(d -> to.at(d).map(p -> p.is(piece.color())).orElse(false))
                .findFirst());
    }

    private List<Pair<Pos, Piece>> findMovedPieces() {
        Map<Pos, Piece> fromPlayerPieces = from.piecesOf(fromGame.player);
        Map<Pos, Piece> toPlayerPieces = to.piecesOf(fromGame.player);

        return fromPlayerPieces.entrySet().stream()
            .map(e -> {
                Pos pos = e.getKey();
                Piece piece = e.getValue();
                Piece newPiece = toPlayerPieces.get(pos);
                if (newPiece == null) return Pair.of(pos, piece);
                if (!piece.equals(newPiece)) return Pair.of(pos, piece);
                return null;
            }).filter(p -> p != null).toList();
    }
}

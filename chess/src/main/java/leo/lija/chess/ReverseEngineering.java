package leo.lija.chess;


import leo.lija.chess.utils.Pair;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class ReverseEngineering {

    private final Game from;
    private final Game to;

    public Optional<Pair<Pos, Pos>> move() {
        if (to.turns != from.turns + 1) return Optional.empty();
        return findMove();
    }

    private Optional<Pair<Pos, Pos>> findMove() {
        List<Pair<Pos, Piece>> movedPieces = findMovedPieces();
        if (movedPieces.size() != 1) return Optional.empty();
        Pos pos = movedPieces.get(0).getFirst();
        Piece piece = movedPieces.get(0).getSecond();
        return findPieceNewPos(pos, piece).map(np -> Pair.of(pos, np));
    }

    private Optional<Pos> findPieceNewPos(Pos pos, Piece piece) {
        return Optional.ofNullable(from.situation().destinations().get(pos))
            .flatMap(dests -> dests.stream()
                .filter(d -> to.board.at(d).map(p -> p.is(piece.color())).orElse(false))
                .findFirst());
    }

    private List<Pair<Pos, Piece>> findMovedPieces() {
        Map<Pos, Piece> fromPlayerPieces = from.board.piecesOf(from.player);
        Map<Pos, Piece> toPlayerPieces = to.board.piecesOf(from.player);

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

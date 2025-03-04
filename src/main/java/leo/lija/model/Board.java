package leo.lija.model;

import leo.lija.exceptions.ChessRulesException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static leo.lija.model.Role.BISHOP;
import static leo.lija.model.Role.KING;
import static leo.lija.model.Role.KNIGHT;
import static leo.lija.model.Role.PAWN;
import static leo.lija.model.Role.QUEEN;
import static leo.lija.model.Role.ROOK;

@AllArgsConstructor
public class Board {

    @Getter
    private Map<Pos, Piece> pieces = new HashMap<>();

    public Optional<Piece> at (Pos at) {
        return Optional.ofNullable(pieces.get(at));
    }

    public Optional<Piece> at(int x, int y) {
        return at(new Pos(x, y));
    }

    public Board placeAt(Piece piece, Pos at) {
        if (pieces.containsKey(at)) throw new ChessRulesException("Cannot move to occupied " + at);
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        piecesNew.put(at, piece);
        return new Board(piecesNew);
    }

    public Board take(Pos at) {
        if (!pieces.containsKey(at)) throw new ChessRulesException("No piece at " + at + " to move");
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        piecesNew.remove(at);
        return new Board(piecesNew);
    }

    public Board moveTo(Pos orig, Pos dest) {
        if (!pieces.containsKey(orig)) throw new ChessRulesException("No piece at " + orig + " to move");
        if (pieces.containsKey(dest)) throw new ChessRulesException("Cannot move to occupied " + dest);
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        piecesNew.put(dest, piecesNew.remove(orig));
        return new Board(piecesNew);
    }

    public Board() {
        Map<Pos, Piece> piecesNew = new HashMap<>();
        List<Role> lineUp = List.of(ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK);
        for (int y: List.of(1, 2, 7, 8)) {
            for (int x = 1; x <= 8; x++) {
                Piece piece = null;
                switch (y) {
                    case 1:
                        piece = new Piece(Color.WHITE, lineUp.get(x - 1));
                        break;
                    case 2:
                        piece = new Piece(Color.WHITE, PAWN);
                        break;
                    case 7:
                        piece = new Piece(Color.BLACK, PAWN);
                        break;
                    case 8:
                        piece = new Piece(Color.BLACK, lineUp.get(x - 1));
                        break;
                    default:
                }
                piecesNew.put(new Pos(x, y), piece);
            }
        }
        this.pieces = piecesNew;
    }
}

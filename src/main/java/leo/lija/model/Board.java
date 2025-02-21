package leo.lija.model;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static leo.lija.model.Role.Bishop;
import static leo.lija.model.Role.King;
import static leo.lija.model.Role.Knight;
import static leo.lija.model.Role.Pawn;
import static leo.lija.model.Role.Queen;
import static leo.lija.model.Role.Rook;

@AllArgsConstructor
public class Board {

    private Map<Pos, Piece> pieces = new HashMap<>();
    private List<Piece> taken = new ArrayList<>();

    public Board placeAt(Piece piece, Pos at) {
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        List<Piece> takenNew = new ArrayList<>(taken);
        piecesNew.put(at, piece);
        return new Board(piecesNew, takenNew);
    }

    public Board take(Pos at) {
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        List<Piece> takenNew = new ArrayList<>(taken);
        piecesNew.remove(at);
        takenNew.add(pieces.get(at));
        return new Board(piecesNew, takenNew);
    }

    public Board moveTo(Pos orig, Pos dest) {
        if (!pieces.containsKey(orig)) throw new RuntimeException("No piece at " + orig + " to move");
        if (pieces.containsKey(dest)) throw new RuntimeException("Cannot move to occupied " + dest);
        Map<Pos, Piece> piecesNew = new HashMap<>(pieces);
        piecesNew.put(dest, piecesNew.remove(orig));
        List<Piece> takenNew = new ArrayList<>(taken);
        return new Board(piecesNew, takenNew);
    }

    public Board reset() {
        Map<Pos, Piece> piecesNew = new HashMap<>();
        List<Role> lineUp = List.of(Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook);
        for (int y: List.of(1, 2, 7, 8)) {
            for (int x = 1; x <= 8; x++) {
                Piece piece = null;
                switch (y) {
                    case 1:
                        piece = new Piece(Color.White, lineUp.get(x - 1));
                        break;
                    case 2:
                        piece = new Piece(Color.White, Pawn);
                        break;
                    case 7:
                        piece = new Piece(Color.Black, Pawn);
                        break;
                    case 8:
                        piece = new Piece(Color.Black, lineUp.get(x - 1));
                        break;
                }
                piecesNew.put(new Pos(x, y), piece);
            }
        }
        return new Board(piecesNew, List.of());
    }
}

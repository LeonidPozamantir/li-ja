package leo.lija.model;

import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;

@AllArgsConstructor
public class Game {

    private Board board;
    private List<Pair<Pos, Pos>> history;
    private Color nextMove;

    private final List<Color> players = List.of(WHITE, BLACK);

    public Game() {
        this(new Board(), List.of(), WHITE);
    }

    public Map<Pos, Set<Pos>> possibleMoves() {
        Map<Pos, Set<Pos>> res = new HashMap<>();
        board.getPieces().entrySet().forEach(entry -> {
            Pos pos = entry.getKey();
            Piece piece = entry.getValue();
            if (piece.color().equals(nextMove)) {
                res.put(pos, movesFrom(pos));
            }
        });
        return res;
    }

    public Set<Pos> movesFrom(Pos pos) {
        return Set.of();
    }
}

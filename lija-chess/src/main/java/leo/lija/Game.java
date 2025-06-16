package leo.lija;

import lombok.AllArgsConstructor;

import java.util.List;

import static leo.lija.Color.BLACK;
import static leo.lija.Color.WHITE;

@AllArgsConstructor
public class Game {

    private Board board;
    private Color player;
    private List<String> pgnMoves;

    private final List<Color> players = List.of(WHITE, BLACK);

    public Game(Board board, Color player) {
        this(board, player, List.of());
    }
    public Game() {
        this(new Board(), WHITE);
    }

    public Situation situation() {
        return board.as(player);
    }
}

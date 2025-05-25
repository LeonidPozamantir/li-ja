package leo.lija.model;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;

@AllArgsConstructor
public class Game {

    private Board board;
    private Color player;

    private final List<Color> players = List.of(WHITE, BLACK);

    public Game() {
        this(new Board(), WHITE);
    }

    public Situation situation() {
        return board.as(player);
    }
}

package leo.lija.model;

import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;

@AllArgsConstructor
public class Game {

    private Board board;
    private List<Pair<Pos, Pos>> history;
    private Color nextPlayer;

    private final List<Color> players = List.of(WHITE, BLACK);

    public Game() {
        this(new Board(), List.of(), WHITE);
    }

    public Map<Pos, Set<Pos>> moves() {
        return board.actors().entrySet().stream()
            .filter(e -> e.getValue().is(nextPlayer))
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().moves()));
    }
}

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

    List<Actor> actors() {
        return board.actorsOf(player);
    }

    public Map<Pos, Set<Pos>> moves() {
        return actors().stream()
            .collect(Collectors.toMap(Actor::getPos, Actor::moves));
    }

    public boolean check() {
        return board.kingPosOf(player).map(king -> board.actorsOf(player.getOpposite()).stream().noneMatch(a -> a.threatens(king)))
            .orElse(false);
    }

    public boolean checkmate() {
        return check() && moves().isEmpty();
    }

    public boolean stalemate() {
        return !check() && moves().isEmpty();
    }
}

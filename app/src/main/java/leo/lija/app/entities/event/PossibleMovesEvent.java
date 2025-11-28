package leo.lija.app.entities.event;

import leo.lija.chess.Color;
import leo.lija.chess.Pos;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@EqualsAndHashCode
public class PossibleMovesEvent implements Event {
    private Color color;
    private Map<Pos, List<Pos>> moves;

    @Override
    public String typ() {
        return "possible_moves";
    }

    @Override
    public Map<String, Object> data() {
        if (moves.isEmpty()) return null;
        return moves.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().key(), e -> e.getValue().stream().map(Pos::key).collect(Collectors.joining())));
    }

    @Override
    public Optional<Color> only() {
        return Optional.of(color);
    }
}

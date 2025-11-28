package leo.lija.app.entities.event;

import leo.lija.chess.Color;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
public class StateEvent implements Event {

    private Color color;
    private int turns;

    @Override
    public String typ() {
        return "state";
    }

    @Override
    public Map<String, Object> data() {
        return Map.of(
            "color", color.name(),
            "turns", turns
        );
    }
}

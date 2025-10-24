package leo.lija.app.entities.event;

import leo.lija.chess.Color;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
public class MoretimeEvent implements Event {
    private Color color;
    private int seconds;

    @Override
    public String encode() {
        return "T" + color.getLetter() + seconds;
    }

    @Override
    public Map<String, Object> export() {
        return Map.of(
            "type", "moretime",
            "color", color.name(),
            "seconds", seconds
        );
    }
}

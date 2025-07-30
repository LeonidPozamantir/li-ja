package leo.lija.system.entities.event;

import leo.lija.chess.Color;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class MoretimeEvent implements Event {
    private Color color;
    private int seconds;

    @Override
    public String encode() {
        return "T" + color.getLetter() + seconds;
    }
}

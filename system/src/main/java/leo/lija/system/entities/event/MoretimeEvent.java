package leo.lija.system.entities.event;

import leo.lija.chess.Color;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
public class MoretimeEvent implements Event {
    private Color color;
    private int seconds;

    @Override
    public Optional<String> encode() {
        return Optional.of("T" + color.getLetter() + seconds);
    }
}

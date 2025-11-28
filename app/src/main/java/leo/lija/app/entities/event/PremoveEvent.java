package leo.lija.app.entities.event;

import leo.lija.chess.Color;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class PremoveEvent extends EmptyEvent {

    private final Color color;

    @Override
    public String typ() {
        return "premove";
    }

    @Override
    public Optional<Color> only() {
        return Optional.of(color);
    }
}

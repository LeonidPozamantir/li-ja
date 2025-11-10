package leo.lija.app.entities.event;

import leo.lija.chess.Color;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
public class RedirectEvent implements Event {
    private Color color;
    private String url;

    @Override
    public String typ() {
        return "redirect";
    }

    @Override
    public String data() {
        return url;
    }

    @Override
    public Optional<Color> only() {
        return Optional.of(color);
    }
}

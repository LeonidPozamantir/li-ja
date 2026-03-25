package leo.lija.app.entities.event;

import leo.lija.chess.Color;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
public class RedirectOwnerEvent extends RedirectEvent {

    private Color color;

    public RedirectOwnerEvent(Color color, String url) {
        super(url);
        this.color = color;
    }

    @Override
    public Optional<Color> only() {
        return Optional.of(color);
    }

    @Override
    public boolean owner() {
        return true;
    }
}

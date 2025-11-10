package leo.lija.app.entities.event;

import leo.lija.chess.Color;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode
@RequiredArgsConstructor
public class ReloadTableEvent implements Event {

    private final Color color;

    @Override
    public String typ() {
        return "reload_table";
    }

    @Override
    public Map<String, Object> data() {
        return null;
    }

    @Override
    public Optional<Color> only() {
        return Optional.of(color);
    }
}

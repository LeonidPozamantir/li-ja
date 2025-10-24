package leo.lija.app.entities.event;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode
public class ThreefoldEvent implements Event {
    @Override
    public String encode() {
        return "t";
    }

    @Override
    public Map<String, Object> export() {
        return Map.of(
            "type", "threefold_repetition"
        );
    }
}

package leo.lija.system.entities.event;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode
public class EndEvent implements Event {
    @Override
    public String encode() {
        return "e";
    }

    @Override
    public Map<String, Object> export() {
        return Map.of(
            "type", "end"
        );
    }
}

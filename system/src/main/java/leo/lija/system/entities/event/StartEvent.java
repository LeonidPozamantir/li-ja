package leo.lija.system.entities.event;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode
public class StartEvent implements Event {
    @Override
    public String encode() {
        return "s";
    }

    @Override
    public Map<String, Object> export() {
        return Map.of(
            "type", "start"
        );
    }
}

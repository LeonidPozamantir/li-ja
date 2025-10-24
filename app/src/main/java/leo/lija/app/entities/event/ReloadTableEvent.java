package leo.lija.app.entities.event;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode
public class ReloadTableEvent implements Event {
    @Override
    public String encode() {
        return "R";
    }

    @Override
    public Map<String, Object> export() {
        return Map.of(
            "type", "reload_table"
        );
    }
}

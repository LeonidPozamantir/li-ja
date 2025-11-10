package leo.lija.app.entities.event;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode
public class EndEvent implements Event {
    @Override
    public String typ() {
        return "end";
    }

    @Override
    public Map<String, Object> data() {
        return null;
    }
}

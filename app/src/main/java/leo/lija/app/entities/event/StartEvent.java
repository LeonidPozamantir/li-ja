package leo.lija.app.entities.event;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode
public class StartEvent implements Event {
    @Override
    public String typ() {
        return "start";
    }

    @Override
    public Map<String, Object> data() {
        return null;
    }
}

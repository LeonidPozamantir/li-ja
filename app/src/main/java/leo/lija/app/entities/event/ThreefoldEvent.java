package leo.lija.app.entities.event;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode
public class ThreefoldEvent implements Event {
    @Override
    public String typ() {
        return "threefold_repetition";
    }

    @Override
    public Map<String, Object> data() {
        return null;
    }
}

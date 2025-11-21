package leo.lija.app.entities.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
public class CrowdEvent implements Event {

    private boolean white;
    private boolean black;
    private int watchers;

    @Override
    public String typ() {
        return "crowd";
    }

    @Override
    public Map<String, Object> data() {
        return Map.of(
            "white", white,
            "black", black,
            "watchers", watchers
        );
    }
}

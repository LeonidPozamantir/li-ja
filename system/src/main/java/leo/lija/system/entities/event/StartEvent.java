package leo.lija.system.entities.event;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class StartEvent implements Event {
    @Override
    public String encode() {
        return "s";
    }
}

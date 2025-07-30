package leo.lija.system.entities.event;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class EndEvent implements Event {
    @Override
    public String encode() {
        return "e";
    }
}

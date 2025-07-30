package leo.lija.system.entities.event;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ThreefoldEvent implements Event {
    @Override
    public String encode() {
        return "t";
    }
}

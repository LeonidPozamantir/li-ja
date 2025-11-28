package leo.lija.app.entities.event;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class EndEvent extends EmptyEvent {
    @Override
    public String typ() {
        return "end";
    }
}

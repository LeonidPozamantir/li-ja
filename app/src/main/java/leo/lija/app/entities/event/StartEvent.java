package leo.lija.app.entities.event;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class StartEvent extends EmptyEvent {
    @Override
    public String typ() {
        return "start";
    }
}

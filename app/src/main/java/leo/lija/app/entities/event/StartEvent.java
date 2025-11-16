package leo.lija.app.entities.event;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = false)
public class StartEvent extends EmptyEvent {
    @Override
    public String typ() {
        return "start";
    }
}

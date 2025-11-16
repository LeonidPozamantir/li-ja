package leo.lija.app.entities.event;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = false)
public class EndEvent extends EmptyEvent {
    @Override
    public String typ() {
        return "end";
    }
}

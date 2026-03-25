package leo.lija.app.entities.event;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class ReloadEvent extends EmptyEvent {
    @Override
    public String typ() {
        return "reload";
    }
}

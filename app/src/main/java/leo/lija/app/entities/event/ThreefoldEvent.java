package leo.lija.app.entities.event;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class ThreefoldEvent extends EmptyEvent {
    @Override
    public String typ() {
        return "threefold_repetition";
    }
}

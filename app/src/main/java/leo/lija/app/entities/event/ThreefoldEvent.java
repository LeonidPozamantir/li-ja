package leo.lija.app.entities.event;

import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = false)
public class ThreefoldEvent extends EmptyEvent {
    @Override
    public String typ() {
        return "threefold_repetition";
    }
}

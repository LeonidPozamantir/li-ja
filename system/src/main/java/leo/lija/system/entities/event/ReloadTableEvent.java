package leo.lija.system.entities.event;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ReloadTableEvent implements Event {
    @Override
    public String encode() {
        return "R";
    }
}

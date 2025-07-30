package leo.lija.system.entities.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class RedirectEvent implements Event {
    private String url;

    @Override
    public String encode() {
        return "r" + url;
    }
}

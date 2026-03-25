package leo.lija.app.entities.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public abstract class RedirectEvent implements Event {

    String url;

    @Override
    public String typ() {
        return "redirect";
    }

    @Override
    public String data() {
        return url;
    }
}

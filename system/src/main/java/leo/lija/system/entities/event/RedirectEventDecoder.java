package leo.lija.system.entities.event;

import java.util.Optional;

public class RedirectEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        return Optional.of(new RedirectEvent(str));
    }
}

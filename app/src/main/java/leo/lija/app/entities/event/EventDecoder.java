package leo.lija.app.entities.event;

import java.util.Optional;

public interface EventDecoder {
    Optional<Event> decode(String str);
}

package leo.lija.system.entities.event;

import java.util.Optional;

public interface EventDecoder {
    Optional<Event> decode(String str);
}

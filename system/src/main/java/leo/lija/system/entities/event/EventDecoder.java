package leo.lija.system.entities.event;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public abstract class EventDecoder {
    public abstract Optional<Event> decode(String str);

    public static final Map<Character, EventDecoder> all = Collections.unmodifiableMap(Map.of(
        's', new StartEventDecoder(),
        'm', new MoveEventDecoder(),
        'p', new PossibleMovesEventDecoder(),
        'E', new EnpassantEventDecoder()
    ));
}

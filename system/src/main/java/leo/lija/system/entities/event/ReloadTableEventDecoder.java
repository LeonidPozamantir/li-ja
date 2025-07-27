package leo.lija.system.entities.event;

import java.util.Optional;

public class ReloadTableEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        return Optional.of(new ReloadTableEvent());
    }
}

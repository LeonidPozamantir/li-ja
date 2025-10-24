package leo.lija.app.entities.event;

import java.util.Optional;

public class ThreefoldEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        return Optional.of(new ThreefoldEvent());
    }
}

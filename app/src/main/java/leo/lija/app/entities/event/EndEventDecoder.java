package leo.lija.app.entities.event;

import java.util.Optional;

public class EndEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        return Optional.of(new EndEvent());
    }
}

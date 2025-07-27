package leo.lija.system.entities.event;

import leo.lija.system.Piotr;

import java.util.Optional;

public class CheckEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        if (str.isEmpty()) return Optional.empty();
        char p = str.charAt(0);
        return Optional.ofNullable(Piotr.decodePos.get(p)).map(pos -> new CheckEvent(pos));
    }
}

package leo.lija.system.entities.event;

import leo.lija.system.Piotr;

import java.util.Optional;

public class EnpassantEventDecoder extends EventDecoder {
    @Override
    public Optional<Event> decode(String str) {;
        if (str.isEmpty()) return Optional.empty();
        char k = str.charAt(0);
        return Optional.ofNullable(Piotr.decodePos.get(k)).map(killed -> new EnpassantEvent(killed));
    }
}

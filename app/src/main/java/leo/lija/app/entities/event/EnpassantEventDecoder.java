package leo.lija.app.entities.event;

import leo.lija.chess.Pos;

import java.util.Optional;

public class EnpassantEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        if (str.isEmpty()) return Optional.empty();
        char k = str.charAt(0);
        return Pos.piotr(k).map(killed -> new EnpassantEvent(killed));
    }
}

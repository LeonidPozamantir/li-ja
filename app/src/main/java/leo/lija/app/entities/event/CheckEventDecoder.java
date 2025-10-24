package leo.lija.app.entities.event;

import leo.lija.chess.Pos;

import java.util.Optional;

public class CheckEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        if (str.isEmpty()) return Optional.empty();
        char p = str.charAt(0);
        return Pos.piotr(p).map(pos -> new CheckEvent(pos));
    }
}

package leo.lija.system.entities.event;

import leo.lija.chess.Color;
import leo.lija.system.Piotr;

import java.util.Optional;

public class MoveEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        char[] chars = str.toCharArray();
        if (chars.length != 3) return Optional.empty();
        char o = chars[0];
        char d = chars[1];
        char c = chars[2];
        return Optional.ofNullable(Piotr.decodePos.get(o))
            .flatMap(orig -> Optional.ofNullable(Piotr.decodePos.get(d))
                .flatMap(dest -> Color.apply(c)
                    .map(color -> new MoveEvent(orig, dest, color))));
    }
}

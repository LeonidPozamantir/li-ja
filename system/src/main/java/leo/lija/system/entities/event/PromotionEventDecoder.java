package leo.lija.system.entities.event;

import leo.lija.chess.Role;
import leo.lija.system.Piotr;

import java.util.Optional;

public class PromotionEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        char[] chars = str.toCharArray();
        if (chars.length != 2) return Optional.empty();
        char r = chars[0];
        char p = chars[1];
        return Role.promotable(r)
            .flatMap(role -> Optional.ofNullable(Piotr.decodePos.get(p))
                .map(pos -> new PromotionEvent(role, pos)));
    }
}

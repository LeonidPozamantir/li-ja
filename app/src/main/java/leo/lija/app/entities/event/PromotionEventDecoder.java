package leo.lija.app.entities.event;

import leo.lija.chess.Pos;
import leo.lija.chess.Role;

import java.util.Optional;

public class PromotionEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        char[] chars = str.toCharArray();
        if (chars.length != 2) return Optional.empty();
        char p = chars[0];
        char r = chars[1];
        return Pos.piotr(p)
            .flatMap(pos -> Role.promotable(r)
                .map(role -> new PromotionEvent(role, pos)));
    }
}

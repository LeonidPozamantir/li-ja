package leo.lija.system.entities.event;

import leo.lija.chess.Color;
import leo.lija.chess.Role;

import java.util.Optional;

public class MoretimeEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        if (str.length() < 2) return Optional.empty();
        char c = str.charAt(0);
        return Color.apply(c)
            .flatMap(color -> {
                try {
                    int seconds = Integer.parseInt(str.substring(1));
                    return Optional.of(new MoretimeEvent(color, seconds));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            });
    }
}

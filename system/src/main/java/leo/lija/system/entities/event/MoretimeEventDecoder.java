package leo.lija.system.entities.event;

import leo.lija.chess.Color;
import leo.lija.system.Utils;

import java.util.Optional;

public class MoretimeEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        if (str.length() < 2) return Optional.empty();
        char c = str.charAt(0);
        return Color.apply(c)
            .flatMap(color -> Utils.parseIntOption(str.substring(1))
                .map(seconds -> new MoretimeEvent(color, seconds)));
    }
}
package leo.lija.app.entities.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventDecoderMap {
    public static final Map<Character, EventDecoder> all = Collections.unmodifiableMap(Map.ofEntries(
        Map.entry('s', new StartEventDecoder()),
        Map.entry('p', new PossibleMovesEventDecoder()),
        Map.entry('P', new PromotionEventDecoder()),
        Map.entry('r', new RedirectEventDecoder()),
        Map.entry('R', new ReloadTableEventDecoder()),
        Map.entry('m', new MoveEventDecoder()),
        Map.entry('M', new MessageEventDecoder()),
        Map.entry('c', new CastlingEventDecoder()),
        Map.entry('C', new CheckEventDecoder()),
        Map.entry('t', new ThreefoldEventDecoder()),
        Map.entry('T', new MoretimeEventDecoder()),
        Map.entry('e', new EndEventDecoder()),
        Map.entry('E', new EnpassantEventDecoder())
    ));
}

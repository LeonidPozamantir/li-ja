package leo.lija.system.entities;

import leo.lija.chess.Move;
import leo.lija.system.entities.event.Event;
import leo.lija.system.entities.event.EventDecoder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@EqualsAndHashCode
public class EventStack {
    private final Map<Integer, Event> events;

    public String encode() {
        return events.entrySet().stream()
            .map(e -> {
                Integer version = e.getKey();
                Event event = e.getValue();
                return event.encode().map(code -> version.toString() + code);
            }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.joining("|"));
    }

    public EventStack withMove(Move move) {
        return this;
    }

    private static final Pattern EVENT_ENCODING = Pattern.compile("^(\\d+)(\\w)(.*)$");

    public static EventStack decode(String evts) {
        return new EventStack(
            Arrays.stream(evts.split("\\|"))
                .map(evt -> {
                    Matcher matcher = EVENT_ENCODING.matcher(evt);
                    if (!matcher.find()) {
                        Optional<Map.Entry<Integer, Event>> res = Optional.empty();
                        return res;
                    }
                    String version = matcher.group(1);
                    String code = matcher.group(2);
                    String data = matcher.group(3);
                    return Optional.ofNullable(EventDecoder.all.get(code.charAt(0)))
                        .flatMap(decoder -> decoder.decode(data)
                            .map(event -> Map.entry(Integer.valueOf(version), event)));
                }).filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    public static EventStack apply(Event ...events) {
        return new EventStack(
            IntStream.range(0, events.length)
                .boxed()
                .collect(Collectors.toMap(Function.identity(), i -> events[i]))
        );
    }
}

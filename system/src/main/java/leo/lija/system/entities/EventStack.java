package leo.lija.system.entities;

import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;
import leo.lija.system.Utils;
import leo.lija.system.entities.event.Event;
import leo.lija.system.entities.event.EventDecoderMap;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@EqualsAndHashCode
public class EventStack {
    private final List<Pair<Integer, Event>> events;

    public String encode() {
        return events.stream()
            .map(e -> {
                Integer version = e.getFirst();
                Event event = e.getSecond();
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
                        Optional<Pair<Integer, Event>> res = Optional.empty();
                        return res;
                    }
                    String v = matcher.group(1);
                    String code = matcher.group(2);
                    String data = matcher.group(3);
                    return Utils.parseIntOption(v)
                        .flatMap(version -> Optional.ofNullable(EventDecoderMap.all.get(code.charAt(0)))
                            .flatMap(decoder -> decoder.decode(data)
                                .map(event -> Pair.of(version, event))));
                }).filter(Optional::isPresent).map(Optional::get)
                .toList()
        );
    }

    public static EventStack apply(Event ...events) {
        return new EventStack(
            IntStream.range(0, events.length)
                .boxed()
                .map(i -> Pair.of(i, events[i]))
                .toList()
        );
    }
}

package leo.lija.system.entities;

import leo.lija.chess.utils.Pair;
import leo.lija.system.Utils;
import leo.lija.system.entities.event.Event;
import leo.lija.system.entities.event.EventDecoderMap;
import leo.lija.system.entities.event.PossibleMovesEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.digester.ArrayStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class EventStack {
    private final List<Pair<Integer, Event>> events;

    public String encode() {
        return events.stream()
            .map(e -> {
                Integer version = e.getFirst();
                Event event = e.getSecond();
                return version.toString() + event.encode();
            }).collect(Collectors.joining("|"));
    }

    public EventStack optimize() {
        final boolean[] previous = {false};
        return new EventStack(
          events.reversed().stream().limit(MAX_EVENTS)
              .map(e -> {
                  Pair<Integer, Event> res;
                 if (e.getSecond() instanceof PossibleMovesEvent && previous[0]) res = Pair.of(e.getFirst(), new PossibleMovesEvent(Map.of()));
                 else if (e.getSecond() instanceof PossibleMovesEvent) {
                     previous[0] = true;
                     res = Pair.of(e.getFirst(), e.getSecond());
                 }
                 else res = e;
                 return res;
              }).collect(Collectors.toCollection(ArrayList::new)).reversed()
        );
    }

    public Integer version() {
        if (events.isEmpty()) return 0;
        return events.getLast().getFirst();
    }

    public EventStack withEvents(List<Event> newEvents) {
        Integer[] v = {version()};
        events.addAll(newEvents.stream().map(e -> {
            v[0]++;
            return Pair.of(v[0], e);
        }).toList());
        return this;
    }

    public static final int MAX_EVENTS = 16;

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
                .collect(Collectors.toCollection(ArrayStack::new))
        );
    }

    public static EventStack apply() {
        return new EventStack(new ArrayList<>());
    }

    public static EventStack build(Event ...events) {
        return new EventStack(
            IntStream.range(0, events.length)
                .boxed()
                .map(i -> Pair.of(i, events[i]))
                .toList()
        );
    }
}

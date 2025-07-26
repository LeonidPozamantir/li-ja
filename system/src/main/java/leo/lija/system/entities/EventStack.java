package leo.lija.system.entities;

import leo.lija.chess.Move;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@EqualsAndHashCode
public class EventStack {
    private final List<Event> events;

    public String encode() {
        return "";
    }

    public EventStack withMove(Move move) {
        return this;
    }

    public static EventStack decode(String evts) {
        return new EventStack(List.of());
    }
}

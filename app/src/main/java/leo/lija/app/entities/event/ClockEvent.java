package leo.lija.app.entities.event;

import leo.lija.chess.Clock;
import leo.lija.chess.Color;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Map;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;

@AllArgsConstructor
@EqualsAndHashCode
public class ClockEvent implements Event {
    private float white;
    private float black;

    @Override
    public String typ() {
        return "clock";
    }

    @Override
    public Map<String, Object> data() {
        return Map.of(
            "white", white,
            "black", black
        );
    }

    public static ClockEvent apply(Clock clock) {
        return new ClockEvent(
            clock.remainingTime(WHITE),
            clock.remainingTime(BLACK)
        );
    }
}

package leo.lija.system.entities;

import jakarta.persistence.Column;
import leo.lija.chess.Clock;
import leo.lija.chess.Color;
import leo.lija.chess.PausedClock;
import leo.lija.chess.RunningClock;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Data
public class RawDbClock {

    private String color;           // c
    private Integer increment;      // i
    @Column(name = "time_limit")
    private Integer limit;          // l
    private float white;            // w
    private float black;            // b
    private Double timer;

    public Optional<Clock> decode() {
        return Optional.ofNullable(color).flatMap(c -> Color.apply(color)
            .map(trueColor ->
                        timer == 0
                            ? new PausedClock(limit, increment, trueColor, white, black)
                            : new RunningClock(limit, increment, trueColor, white, black, timer)
                    ));
    }

    public static RawDbClock encode(Clock clock) {
        return new RawDbClock(
            clock.getColor().getName(),
            clock.getIncrement(),
            clock.getLimit(),
            clock.getWhiteTime(),
            clock.getBlackTime(),
            clock.getTimer()
        );
    }
}

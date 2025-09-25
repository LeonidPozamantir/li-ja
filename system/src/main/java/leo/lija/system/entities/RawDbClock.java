package leo.lija.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import leo.lija.chess.Clock;
import leo.lija.chess.Color;
import leo.lija.chess.PausedClock;
import leo.lija.chess.RunningClock;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Getter
public class RawDbClock {

    private String color;
    private Integer increment;
    @Column(name = "time_limit")
    private Integer limit;
    private float white;
    private float black;
    private Double timer;

    public Optional<Clock> decode() {
        return Optional.ofNullable(color).flatMap(c -> Color.apply(color)
            .map(trueColor ->
                        timer == 0
                            ? new PausedClock(limit * 1000, increment * 1000, trueColor, Math.round(white * 1000), Math.round(black * 1000))
                            : new RunningClock(limit * 1000, increment * 1000, trueColor, Math.round(white * 1000), Math.round(black * 1000), (long) (timer * 1000))
                    ));
    }

    public static RawDbClock encode(Clock clock) {
        return new RawDbClock(
            clock.getColor().getName(),
            clock.getIncrement() / 1000,
            clock.getLimit() / 1000,
            clock.getWhiteTime() / 1000f,
            clock.getBlackTime() / 1000f,
            clock.getTimer() / 1000d
        );
    }
}

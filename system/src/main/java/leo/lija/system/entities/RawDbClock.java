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
    @ElementCollection
    private Map<String, Integer> times;
    private Long timer;

    public Optional<Clock> decode() {
        return Optional.ofNullable(color).flatMap(c -> Color.apply(color)
            .flatMap(trueColor -> Optional.ofNullable(times.get("white"))
                .flatMap(whiteTime -> Optional.ofNullable(times.get("black"))
                    .map(blackTime ->
                        timer == 0
                            ? new PausedClock(limit, increment, trueColor, whiteTime, blackTime)
                            : new RunningClock(limit, increment, trueColor, whiteTime, blackTime, timer)
                    ))));
    }

    public static RawDbClock encode(Clock clock) {
        return new RawDbClock(
            clock.getColor().getName(),
            clock.getIncrement(),
            clock.getLimit(),
            Map.of(
                "white", clock.getWhiteTime(),
                "black", clock.getBlackTime()
            ),
            clock.getTimer()
        );
    }
}

package leo.lija.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import leo.lija.chess.Clock;
import leo.lija.chess.Color;
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
    private Map<String, Float> times;

    public Optional<Clock> decode() {
        return Optional.ofNullable(color).flatMap(c -> Color.apply(color)
            .flatMap(trueColor -> Optional.ofNullable(times.get("white"))
                .flatMap(whiteTime -> Optional.ofNullable(times.get("white"))
                    .map(blackTime -> new Clock(trueColor, increment, limit, whiteTime, blackTime)))));
    }

    public static RawDbClock encode(Clock clock) {
        return new RawDbClock(clock.getColor().getName(), clock.getIncrement(), clock.getLimit(), Map.of("white", clock.getWhiteTime(), "black", clock.getBlackTime()));
    }
}

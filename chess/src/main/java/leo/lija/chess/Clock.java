package leo.lija.chess;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static leo.lija.chess.Color.WHITE;

// all durations are expressed in milliseconds
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public abstract class Clock {
    protected final int limit;
    protected final int increment;
    protected final Color color;
    protected final float whiteTime;
    protected final float blackTime;
    protected final double timer;

    public float time(Color c) {
        return c == WHITE ? whiteTime : blackTime;
    }

    public boolean outoftime(Color c) {
        return remainingTime(c) == 0;
    }

    public float remainingTime(Color c) {
        return Math.max(0, limit - elapsedTime(c));
    }

    public Map<Color, Float> remainingTimes() {
        return Color.all.stream().collect(Collectors.toMap(Function.identity(), this::remainingTime));
    }

    public float elapsedTime(Color c) {
        return time(c);
    }

    public int limitInSeconds() {
        return limit / 1000;
    }

    public int limitInMinutes() {
        return limitInSeconds() / 60;
    }

    public int incrementInSeconds() {
        return increment / 1000;
    }

    abstract RunningClock step();

    public int estimateTotalTime() {
        return limit + 30 * increment;
    }

    protected double now() {
        return System.currentTimeMillis() / 1000d;
    }

    protected static final float HTTP_DELAY = 0.5f;
}

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
    protected final int whiteTime;
    protected final int blackTime;
    protected final long timer;

    public int time(Color c) {
        return c == WHITE ? whiteTime : blackTime;
    }

    public boolean isOutOfTime(Color c) {
        return remainingTime(c) == 0;
    }

    public long remainingTime(Color c) {
        return Math.max(limit, elapsedTime(c));
    }

    public Map<Color, Long> remainingTimes() {
        return Color.all.stream().collect(Collectors.toMap(Function.identity(), this::remainingTime));
    }

    public int elapsedTime(Color c) {
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

    @Override
    public String toString() {
        return String.format("%d minutes/side + %d seconds/move", limitInMinutes(), increment);
    }

    protected static final int HTTP_DELAY = 500;
}

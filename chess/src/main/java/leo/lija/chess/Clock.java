package leo.lija.chess;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static leo.lija.chess.Color.WHITE;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public abstract class Clock {
    protected final Color color;
    protected final int increment;
    protected final int limit;
    protected final long whiteTime;
    protected final long blackTime;
    protected final long timer;

    public long time(Color c) {
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

    public long elapsedTime(Color c) {
        return time(c);
    }

    public int limitInMinutes() {
        return limit / 60;
    }

    public long now() {
        return System.currentTimeMillis();
    }

    public int estimateTotalTime() {
        return limit + 30 * increment;
    }

    @Override
    public String toString() {
        return String.format("%d minutes/side + %d seconds/move", limitInMinutes(), increment);
    }

    protected static final int HTTP_DELAY = 500;
}

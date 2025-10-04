package leo.lija.chess;

import java.util.Optional;

import static leo.lija.chess.Color.WHITE;

public class PausedClock extends Clock {

    public PausedClock(int limit, int increment) {
        this(limit, increment, WHITE, 0, 0);
    }

    public PausedClock(int limit, int increment, Color color, float whiteTime, float blackTime) {
        super(limit, increment, color, whiteTime, blackTime, 0, Optional.empty());
    }

    @Override
    RunningClock step() {
        return new RunningClock(limit, increment, color, whiteTime, blackTime, now()).giveTime(WHITE, increment).step();
    }

    @Override
    public PausedClock stop() {
        return this;
    }

    @Override
    public PausedClock addTime(Color c, float t) {
        return switch (c) {
            case WHITE -> new PausedClock(limit, increment, color, whiteTime + t, blackTime);
            case BLACK -> new PausedClock(limit, increment, color, whiteTime, blackTime + t);
        };
    }

    @Override
    public PausedClock giveTime(Color c, float t) {
        return addTime(c, -t);
    }
}

package leo.lija.chess;

import static leo.lija.chess.Color.WHITE;

public class PausedClock extends Clock {

    public PausedClock(int limit, int increment) {
        this(limit, increment, WHITE, 0, 0);
    }

    public PausedClock(int limit, int increment, Color color, int whiteTime, int blackTime) {
        super(limit, increment, color, whiteTime, blackTime, 0);
    }

    @Override
    RunningClock step() {
        return new RunningClock(limit, increment, color, whiteTime, blackTime).giveTime(WHITE, increment).step();
    }
}

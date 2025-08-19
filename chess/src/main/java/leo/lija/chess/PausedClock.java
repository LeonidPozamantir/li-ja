package leo.lija.chess;

public class PausedClock extends Clock {

    public PausedClock(Color color, int increment, int limit, long whiteTime, long blackTime) {
        super(color, increment, limit, whiteTime, blackTime, 0);
    }
}

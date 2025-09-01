package leo.lija.chess;

public class RunningClock extends Clock {

    public RunningClock(int limit, int increment, Color color, int whiteTime, int blackTime) {
        this(limit, increment, color, whiteTime, blackTime, 0L);
    }

    public RunningClock(int limit, int increment, Color color, int whiteTime, int blackTime, long timer) {
        super(limit, increment, color, whiteTime, blackTime, timer);
    }

    @Override
    public int elapsedTime(Color c) {
        return time(c)
            + (c == color ? (int) (now() - timer) : 0);
    }

    @Override
    public RunningClock step() {
        RunningClock addedTime = addTime(color, Math.max(0, (int) (now() - timer) - HTTP_DELAY - increment));
        return new RunningClock(limit, increment, color.getOpposite(), addedTime.whiteTime, addedTime.blackTime, now());
    }

    public RunningClock addTime(Color c, int t) {
        return switch (c) {
            case WHITE -> new RunningClock(limit, increment, color, whiteTime + t, blackTime, timer);
            case BLACK -> new RunningClock(limit, increment, color, whiteTime, blackTime + t, timer);
        };
    }

    public RunningClock giveTime(Color c, int t) {
        return addTime(c, -t);
    }

    private long now() {
        return System.currentTimeMillis();
    }
}

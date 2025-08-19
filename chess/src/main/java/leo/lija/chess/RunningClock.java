package leo.lija.chess;

public class RunningClock extends Clock {
    public RunningClock(Color color, int increment, int limit, long whiteTime, long blackTime, long timer) {
        super(color, increment, limit, whiteTime, blackTime, timer);
    }

    @Override
    public long elapsedTime(Color c) {
        return time(c)
            + (c == color ? now() - timer : 0);
    }

    public RunningClock step() {
        RunningClock addedTime = addTime(color, Math.max(0, now() - timer - HTTP_DELAY - increment));
        return new RunningClock(color.getOpposite(), increment, limit, addedTime.whiteTime, addedTime.blackTime, now());
    }

    public RunningClock addTime(Color c, long t) {
        return switch (c) {
            case WHITE -> new RunningClock(color, increment, limit, whiteTime + t, blackTime, timer);
            case BLACK -> new RunningClock(color, increment, limit, whiteTime, blackTime + t, timer);
        };
    }

    public RunningClock giveTime(Color c, long t) {
        return addTime(c, -t);
    }

}

package leo.lija.chess;

public class RunningClock extends Clock {

    public RunningClock(int limit, int increment, Color color, float whiteTime, float blackTime) {
        this(limit, increment, color, whiteTime, blackTime, 0L);
    }

    public RunningClock(int limit, int increment, Color color, float whiteTime, float blackTime, double timer) {
        super(limit, increment, color, whiteTime, blackTime, timer);
    }

    @Override
    public float elapsedTime(Color c) {
        return time(c)
            + (c == color ? (float) (now() - timer) : 0);
    }

    @Override
    public RunningClock step() {
        double t = now();
        RunningClock addedTime = addTime(color, Math.max(0, (float) (t - timer) - HTTP_DELAY - increment));
        return new RunningClock(limit, increment, color.getOpposite(), addedTime.whiteTime, addedTime.blackTime, t);
    }

    public RunningClock addTime(Color c, float t) {
        return switch (c) {
            case WHITE -> new RunningClock(limit, increment, color, whiteTime + t, blackTime, timer);
            case BLACK -> new RunningClock(limit, increment, color, whiteTime, blackTime + t, timer);
        };
    }

    public RunningClock giveTime(Color c, float t) {
        return addTime(c, -t);
    }

}

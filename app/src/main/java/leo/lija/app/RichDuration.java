package leo.lija.app;

import java.time.Duration;

public class RichDuration {

    public static Duration randomize(Duration d, float ratio) {
        long m = d.toMillis();
        long m2 = Math.round(m + ratio * m * (2 * Math.random() - 1));
        return Duration.ofMillis(m2);
    }

    public static Duration randomize(Duration d) {
        return randomize(d,0.1f);
    }
}

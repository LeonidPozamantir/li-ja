package leo.lija.app;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RichDuration {

    public static Duration randomize(Duration d, float ratio) {
        return Duration.ofMillis(Utils.approximately(d.toMillis(), ratio));
    }

    public static Duration randomize(Duration d) {
        return randomize(d,0.1f);
    }
}

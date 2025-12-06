package leo.lija.app;

import leo.lija.app.exceptions.AppException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static Optional<Integer> parseIntOption(String str) {
        try {
            return Optional.of(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static final Pattern MOVE_STRING = Pattern.compile("^([a-h][1-8]) ([a-h][1-8])$");

    public static AppException gameNotFound() {
        return new AppException("Game not found");
    }

    public static double approximately(double number, float ratio) {
        return number + ratio * number * (2 * Math.random() - 1);
    }

    public static float approximately(float number, float ratio) {
        return (float) approximately((double) number, ratio);
    }

    public static long approximately(long number, float ratio) {
        return Math.round(approximately((float) number, ratio));
    }

}

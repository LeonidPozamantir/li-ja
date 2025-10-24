package leo.lija.app;

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

}

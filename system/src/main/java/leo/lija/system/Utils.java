package leo.lija.system;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    public static final Pattern MOVE_STRING = Pattern.compile("^([a-h][1-8]) ([a-h][1-8])$");
}

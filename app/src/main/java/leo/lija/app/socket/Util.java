package leo.lija.app.socket;

import leo.lija.app.exceptions.AppException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {

    public static String uid() {
        return IntStream.range(0, 6)
            .map(i -> new Random().nextInt(25) + 97)
            .mapToObj(i -> String.valueOf((char) i))
            .collect(Collectors.joining());
    }

    public static void connectionFail() {
        throw new AppException("Invalid request");
    }
}

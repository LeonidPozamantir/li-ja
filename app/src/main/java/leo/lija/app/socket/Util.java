package leo.lija.app.socket;

import leo.lija.app.exceptions.AppException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {

    public static Map<String, String> PONG = Map.of("t", "p");

    public static void connectionFail() {
        throw new AppException("Invalid request");
    }
}

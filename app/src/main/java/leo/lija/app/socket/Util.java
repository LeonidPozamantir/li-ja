package leo.lija.app.socket;

import leo.lija.app.exceptions.AppException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {

    public static void connectionFail() {
        throw new AppException("Invalid request");
    }
}

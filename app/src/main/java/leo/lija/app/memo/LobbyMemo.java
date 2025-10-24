package leo.lija.app.memo;

import org.springframework.stereotype.Service;

@Service
public class LobbyMemo {

    private int privateVersion = 1;

    public int version() {
        return privateVersion;
    }

    public int increase() {
        privateVersion++;
        return privateVersion;
    }
}

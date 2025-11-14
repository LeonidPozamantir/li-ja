package leo.lija.app.game;

import leo.lija.chess.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
@Getter
public abstract class Member {

    protected final String uid;
    protected final Color color;
    protected final Optional<String> username;
    protected final boolean owner;

    public static Member apply(String uid, Color color, boolean owner, Optional<String> username) {
        if (owner) return new Owner(uid, color, username);
        return new Watcher(uid, color, username);
    }
}

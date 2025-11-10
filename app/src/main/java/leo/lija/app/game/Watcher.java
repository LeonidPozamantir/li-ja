package leo.lija.app.game;

import leo.lija.chess.Color;

import java.util.Optional;

public class Watcher extends Member {
    public Watcher(String uid, Color color, Optional<String> username) {
        super(uid, color, username);
    }
}

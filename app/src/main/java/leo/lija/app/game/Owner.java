package leo.lija.app.game;

import leo.lija.chess.Color;

import java.util.Optional;

public class Owner extends Member {
    public Owner(String uid, Color color, Optional<String> username) {
        super(uid, color, username, true);
    }
}

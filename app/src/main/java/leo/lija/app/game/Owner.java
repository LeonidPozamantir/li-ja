package leo.lija.app.game;

import leo.lija.app.entities.PovRef;
import leo.lija.chess.Color;

import java.util.Optional;

public class Owner extends Member {
    public Owner(String uid, PovRef ref, Optional<String> username) {
        super(uid, ref, username, true);
    }
}

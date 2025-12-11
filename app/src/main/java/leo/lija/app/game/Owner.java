package leo.lija.app.game;

import leo.lija.app.entities.PovRef;

import java.util.Optional;

public class Owner extends Member {
    public Owner(String uid, Optional<String> username, PovRef ref) {
        super(uid, username, ref, true);
    }
}

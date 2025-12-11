package leo.lija.app.game;

import leo.lija.app.entities.PovRef;

import java.util.Optional;

public class Watcher extends Member {
    public Watcher(String uid, Optional<String> username, PovRef ref) {
        super(uid, username, ref, false);
    }
}

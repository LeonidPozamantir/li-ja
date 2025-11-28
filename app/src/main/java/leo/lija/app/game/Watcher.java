package leo.lija.app.game;

import leo.lija.app.entities.PovRef;
import leo.lija.chess.Color;

import java.util.Optional;

public class Watcher extends Member {
    public Watcher(String uid, PovRef ref) {
        super(uid, ref, false);
    }
}

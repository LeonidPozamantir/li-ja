package leo.lija.app.game;

import leo.lija.app.entities.PovRef;
import leo.lija.chess.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class Member {

    protected final String uid;
    protected final PovRef ref;
    protected final boolean owner;

    public boolean watcher() {
        return !owner;
    }

    public String gameId() {
        return ref.gameId();
    }

    public Color color() {
        return ref.color();
    }

    public String className() {
        return owner ? "Owner" : "Watcher";
    }

    @Override
    public String toString() {
        return "%s(%s-%s)".formatted(className(), gameId(), color());
    }

    public static Member apply(String uid, PovRef ref, boolean owner) {
        if (owner) return new Owner(uid, ref);
        return new Watcher(uid, ref);
    }
}

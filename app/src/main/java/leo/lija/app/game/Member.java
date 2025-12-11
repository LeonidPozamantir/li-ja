package leo.lija.app.game;

import leo.lija.app.entities.PovRef;
import leo.lija.app.socket.SocketMember;
import leo.lija.chess.Color;
import lombok.Getter;

import java.util.Optional;

@Getter
public abstract class Member extends SocketMember {

    protected final PovRef ref;
    protected final boolean owner;

    public Member(String uid, Optional<String> username, PovRef ref, boolean owner) {
        super(uid, username);
        this.ref = ref;
        this.owner = owner;
    }

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

    public static Member apply(String uid, Optional<String> username, PovRef ref, boolean owner) {
        if (owner) return new Owner(uid, username, ref);
        return new Watcher(uid, username, ref);
    }
}

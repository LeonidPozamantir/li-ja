package leo.lija.app.lobby;

import leo.lija.app.entities.Hook;
import leo.lija.app.socket.SocketMember;
import lombok.Getter;

import java.util.Optional;

public class Member extends SocketMember {

    @Getter
    private final Optional<String> hookOwnerId;

    public Member(String uid, Optional<String> username, Optional<String> hookOwnerId) {
        super(uid, username);
        this.hookOwnerId = hookOwnerId;
    }

    public boolean ownsHook(Hook hook) {
        return hookOwnerId.isPresent() && hookOwnerId.get().equals(hook.getOwnerId());
    }
}

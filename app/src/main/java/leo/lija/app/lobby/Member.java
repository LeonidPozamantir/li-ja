package leo.lija.app.lobby;

import leo.lija.app.entities.Hook;

import java.util.Optional;

public record Member(String uid, Optional<String> hookOwnerId) {

    public boolean ownsHook(Hook hook) {
        return hookOwnerId.isPresent() && hookOwnerId.get().equals(hook.getOwnerId());
    }
}

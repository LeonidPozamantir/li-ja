package leo.lija.app.lobby;

import jakarta.annotation.PostConstruct;
import leo.lija.app.config.SocketIOService;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Entry;
import leo.lija.app.entities.Hook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Lobby {

    private final Hub hub;
    private final HookPool hookPool;
    private final SocketIOService socketIOService;

    @PostConstruct
    private void init() {
        socketIOService.setLobby(this);
    }

    public void join(String uid, Integer version, Optional<String> hook) {
        hub.join(uid, version, hook);
        hook.ifPresent(hookPool::register);
    }

    public void talk(SocketIOService.LobbyTalkForm event) {
        hub.talk(event.data().txt(), event.data().u());
    }

    public void addEntry(Entry entry) {
        hub.addEntry(entry);
    }

    public void quit(String uid, Optional<String> hook) {
        hook.ifPresent(hookPool::unregister);
        hub.quit(uid);
    }

    public void removeHook(Hook hook) {
        hookPool.unregister(hook.getOwnerId());
        hub.removeHook(hook);
    }

    public void addHook(Hook hook) {
        hub.addHook(hook);
    }

    public void biteHook(Hook hook, DbGame game) {
        hub.biteHook(hook, game);
    }
}

package leo.lija.app.lobby;

import jakarta.annotation.PostConstruct;
import leo.lija.app.config.SocketIOService;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Entry;
import leo.lija.app.entities.Hook;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("lobbySocket")
@RequiredArgsConstructor
public class Socket {

    private final Hub hub;
    private final SocketIOService socketIOService;

    @PostConstruct
    private void init() {
        socketIOService.setLobbySocket(this);
    }

    public void join(String uid, Integer version, Optional<String> username, Optional<String> hook) {
        hub.join(uid, version, username, hook);
    }

    public void talk(SocketIOService.LobbyTalkForm event) {
        if (event.t().equals("talk")) hub.talk(event.d().txt(), event.d().u());
    }

    public void addEntry(Entry entry) {
        hub.addEntry(entry);
    }

    public void quit(String uid) {
        hub.quit(uid);
    }

    public void removeHook(Hook hook) {
        hub.removeHook(hook);
    }

    public void addHook(Hook hook) {
        hub.addHook(hook);
    }

    public void biteHook(Hook hook, DbGame game) {
        hub.biteHook(hook, game);
    }

}

package leo.lija.app.lobby;

import jakarta.annotation.PostConstruct;
import leo.lija.app.config.SocketIOService;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Entry;
import leo.lija.app.entities.Hook;
import leo.lija.app.socket.Util;
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

    public void join(Optional<String> uidOption, Optional<String> username, Optional<Integer> versionOption, Optional<String> hook) {
        if (uidOption.isPresent() && versionOption.isPresent()) {
            hub.join(uidOption.get(), username, versionOption.get(), hook);
        } else Util.connectionFail();
    }

    public void talk(SocketIOService.LobbyTalkForm event) {
        SocketIOService.LobbyTalkForm.Data data = event.d();
        String txt = data.txt();
        String username = data.u();
        hub.talk(txt, username);
    }

    public void ping(String uid) {
        hub.ping(uid);
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

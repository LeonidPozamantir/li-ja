package leo.lija.app.site;

import jakarta.annotation.PostConstruct;
import leo.lija.app.config.SocketIOService;
import leo.lija.app.socket.Util;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Qualifier("siteSocket")
@Service
public class Socket {

    private final SocketIOService socketIOService;

    private final Hub hub;

    public Socket(SocketIOService socketIOService, Hub hub) {
        this.socketIOService = socketIOService;
        this.hub = hub;
    }

    @PostConstruct
    private void init() {
        socketIOService.setSiteSocket(this);
    }

    public void join(Optional<String> uidOption, Optional<String> username) {
        uidOption.ifPresentOrElse(
            uid -> hub.join(uid, username),
            Util::connectionFail
        );
    }

    public void quit(String uid) {
        hub.quit(uid);
    }
}

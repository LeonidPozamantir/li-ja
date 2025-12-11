package leo.lija.app.site;

import leo.lija.app.config.SocketIOService;
import leo.lija.app.socket.HubActor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("siteHub")
public class Hub extends HubActor<Member> implements leo.lija.app.Hub {

    protected Hub(SocketIOService socketService, @Value("${site.uid.timeout}") int timeout) {
        super(socketService, timeout, "site");
    }

    public void join(String uid, Optional<String> username) {
        socketService.addToRoom("site", uid);
        addMember(uid, new Member(uid, username));
    }
}

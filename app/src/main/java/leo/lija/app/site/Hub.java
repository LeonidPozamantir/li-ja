package leo.lija.app.site;

import leo.lija.app.config.SocketIOService;
import leo.lija.app.socket.HubActor;
import leo.lija.app.socket.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service("siteHub")
public class Hub extends HubActor<Member> {

    protected Hub(SocketIOService socketService, @Value("${site.uid.timeout}") int timeout) {
        super(socketService, timeout, "site");
    }

    public void withUsernames(Consumer<List<String>> op) {
        op.accept(usernames());
    }

    public void join(String uid, Optional<String> username) {
        socketService.addToRoom("site", uid);
        members.put(uid, new Member(uid, username));
        setAlive(uid);
    }

    public CompletableFuture<Void> nbMembers() {
        return CompletableFuture.runAsync(() -> notifyAll("n", members.size()));
    }

    public int getNbMembers() {
        return members.size();
    }

    private void notifyAll(String t, Object data) {
        Map<String, Object> msg = makeMessage(t, data);
        socketService.sendMessage("site", msg);
    }

    private List<String> usernames() {
        return members.values().stream()
            .filter(m -> m.username().isPresent())
            .map(m -> m.username().get())
            .toList();
    }

    private Map<String, Object> makeMessage(String t, Object data) {
        return Map.of(
            "t", t,
            "d", data
        );
    }
}

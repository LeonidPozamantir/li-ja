package leo.lija.app.site;

import leo.lija.app.config.SocketIOService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service("siteHub")
@RequiredArgsConstructor
public class Hub {

    private final SocketIOService socketService;

    private Map<String, Member> members = new ConcurrentHashMap<>();

    public void withUsernames(Consumer<List<String>> op) {
        op.accept(usernames());
    }

    public void join(String uid, Optional<String> username) {
        socketService.addToRoom("site", uid);
        members.put(uid, new Member(uid, username));
    }

    public CompletableFuture<Void> nbMembers() {
        return CompletableFuture.runAsync(() -> notifyAll("nbp", members.size()));
    }

    public int getNbMembers() {
        return members.size();
    }

    public void quit(String uid) {
        members.remove(uid);
        socketService.removeFromRoom("site", uid);
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

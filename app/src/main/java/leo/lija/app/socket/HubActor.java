package leo.lija.app.socket;

import leo.lija.app.config.SocketIOService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class HubActor<M extends SocketMember> {

    protected final SocketIOService socketService;
    protected final Map<String, M> members = new ConcurrentHashMap<>();
    private final PingMemo aliveUids;
    Map<String, Object> pong = makePong(0);
    private final String room;

    protected HubActor(SocketIOService socketService, int uidTimeout, String room) {
        this.socketService = socketService;
        this.aliveUids = new PingMemo(uidTimeout);
        this.room = room;
    }

    public void ping(String uid) {
        setAlive(uid);
        socketService.sendMessageToClient(uid, room, pong);
    }

    public void broom() {
        members.keySet().stream().filter(k -> !aliveUids.get(k)).forEach(this::eject);
    }

    public void eject(String uid) {
        quit(uid);
        socketService.removeFromRoom(room, uid);
    }

    public void quit(String uid) {
        members.remove(uid);
    }

    public int getNbMembers() {
        return members.size();
    }

    public void nbMembers(int nb) {
        pong = makePong(nb);
    }

    public List<String> getUsernames() {
        return usernames();
    }

    private void notifyAll(String t, Object data) {
        Map<String, Object> msg = makeMessage(t, data);
        socketService.sendMessage(room, msg);
    }

    protected Map<String, Object> makeMessage(String t, Object data) {
        return Map.of("t", t, "d", data);
    }

    private Map<String, Object> makePong(int nb) {
        return makeMessage("n", nb);
    }

    public void addMember(String uid, M member) {
        eject(uid);
        members.put(uid, member);
        setAlive(uid);
    }

    protected void setAlive(String uid) {
        aliveUids.put(uid);
    }

    private Set<String> uids() {
        return members.keySet();
    }

    private List<String> usernames() {
        return members.values().stream()
            .map(m -> m.username)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }
}

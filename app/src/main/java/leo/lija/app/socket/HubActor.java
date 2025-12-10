package leo.lija.app.socket;

import leo.lija.app.config.SocketIOService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class HubActor<M> {

    protected final SocketIOService socketService;
    protected final Map<String, M> members = new ConcurrentHashMap<>();
    private final PingMemo aliveUids;
    private final String room;

    protected HubActor(SocketIOService socketService, int uidTimeout, String room) {
        this.socketService = socketService;
        this.aliveUids = new PingMemo(uidTimeout);
        this.room = room;
    }

    public void ping(String uid) {
        setAlive(uid);
        socketService.sendMessageToClient(uid, room, Util.PONG);
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
}

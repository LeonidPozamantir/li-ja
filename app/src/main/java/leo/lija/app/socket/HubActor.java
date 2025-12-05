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

    protected HubActor(SocketIOService socketService, int timeout, String room) {
        this.socketService = socketService;
        this.aliveUids = new PingMemo(timeout);
        this.room = room;
    }

    public void ping(String uid) {
        setAlive(uid);
        socketService.sendMessageToClient(uid, room, Util.PONG);
    }

    public void broom() {
        uids().stream()
            .filter(k -> !aliveUids.get(k))
            .forEach(this::quit);
    }

    public void quit(String uid) {
        log.info("quit {}", uid);
        members.remove(uid);
        socketService.removeFromRoom(room, uid);
    }

    protected void setAlive(String uid) {
        aliveUids.put(uid);
    }

    private Set<String> uids() {
        return members.keySet();
    }
}

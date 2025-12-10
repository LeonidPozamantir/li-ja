package leo.lija.app.game;

import leo.lija.app.config.SocketIOService;
import leo.lija.app.entities.event.Event;
import leo.lija.app.socket.HubActor;
import leo.lija.chess.Color;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
public class HubMaster {

    private final SocketIOService socketIOService;
    private final Supplier<History> makeHistory;
    private final int uidTimeout;
    private final int hubTimeout;

    private final Map<String, Hub> hubs = new ConcurrentHashMap<>();

    public HubMaster(SocketIOService socketIOService, Supplier<History> makeHistory, int uidTimeout, int hubTimeout) {
        this.socketIOService = socketIOService;
        this.makeHistory = makeHistory;
        this.uidTimeout = uidTimeout;
        this.hubTimeout = hubTimeout;
    }

    public void broom() {
        hubs.values().forEach(Hub::broom);
    }

    public void events(String gameId, List<Event> events) {
        Optional.ofNullable(hubs.get(gameId)).ifPresent(hub -> hub.events(events));

    }

    public Hub getHub(String gameId) {
        Hub hub = hubs.get(gameId);
        if (hub == null) {
            hub = mkHub(gameId);
            hubs.put(gameId, hub);
        }
        return hub;
    }

    public int getGameVersion(String gameId) {
        return Optional.ofNullable(hubs.get(gameId))
            .map(Hub::getGameVersion)
            .orElse(0);
    }

    public void closeGame(String gameId) {
        Hub hub = hubs.get(gameId);
        if (hub == null) return;
        hubs.remove(gameId);
    }

    public boolean isConnectedOnGame(String gameId, Color color) {
        return Optional.ofNullable(hubs.get(gameId))
            .map(hub -> hub.isConnectedOnGame(color))
            .orElse(false);
    }

    public int getNbHubs() {
        return hubs.size();
    }

    public int getNbMembers() {
        return hubs.values().stream()
            .mapToInt(HubActor::getNbMembers)
            .sum();
    }

    private Hub mkHub(String gameId) {
        return new Hub(this, socketIOService, gameId, makeHistory.get(), uidTimeout, hubTimeout);
    }
}

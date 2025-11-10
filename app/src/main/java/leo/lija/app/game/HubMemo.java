package leo.lija.app.game;

import com.google.common.cache.Cache;
import leo.lija.app.config.SocketIOService;
import leo.lija.app.entities.DbGame;
import leo.lija.app.memo.Builder;
import leo.lija.app.socket.History;

import java.util.function.Supplier;

public class HubMemo {

    private final SocketIOService socketIOService;
    private final Supplier<History> makeHistory;
    private final Cache<String, Hub> cache;

    public HubMemo(SocketIOService socketIOService, Supplier<History> makeHistory, int timeout) {
        this.socketIOService = socketIOService;
        this.makeHistory = makeHistory;
        this.cache = Builder.cache(timeout, this::compute);
    }


    public Hub get(String gameId) {
        return cache.getIfPresent(gameId);
    }

    public Hub getFromFullId(String fullId) {
        return cache.getIfPresent(DbGame.takeGameId(fullId));
    }

    public void put(String gameId, Hub hub) {
        cache.put(gameId, hub);
    }

    private Hub compute(String gameId) {
        return new Hub(socketIOService, gameId, makeHistory.get());
    }
}

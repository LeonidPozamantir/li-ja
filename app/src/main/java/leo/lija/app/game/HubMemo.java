package leo.lija.app.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import leo.lija.app.config.SocketIOService;
import leo.lija.app.entities.DbGame;
import leo.lija.app.socket.History;
import lombok.NonNull;

import java.util.Map;
import java.util.function.Supplier;

public class HubMemo {

    private final SocketIOService socketIOService;
    private final Supplier<History> makeHistory;
    private final LoadingCache<@NonNull String, @NonNull Hub> cache;

    public HubMemo(SocketIOService socketIOService, Supplier<History> makeHistory) {
        this.socketIOService = socketIOService;
        this.makeHistory = makeHistory;
        this.cache = CacheBuilder.newBuilder()
            .build(new CacheLoader<>() {
                public Hub load(String key) {
                    return HubMemo.this.compute(key);
                }
            });
    }

    public Map<String, Hub> all() {
        return cache.asMap();
    }

    public Hub get(String gameId) {
        return cache.getUnchecked(gameId);
    }

    public Hub getFromFullId(String fullId) {
        return get(DbGame.takeGameId(fullId));
    }

    public void remove(String gameId) {
        cache.invalidate(gameId);
    }

    private Hub compute(String gameId) {
        System.out.println("create actor game " + gameId);
        return new Hub(socketIOService, gameId, makeHistory.get());
    }
}

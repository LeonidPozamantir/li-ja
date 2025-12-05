package leo.lija.app.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import leo.lija.app.config.SocketIOService;
import leo.lija.app.entities.DbGame;
import lombok.NonNull;
import org.springframework.core.task.TaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class HubMemo {

    private final TaskExecutor executor;

    private final SocketIOService socketIOService;
    private final Supplier<History> makeHistory;
    private final LoadingCache<@NonNull String, @NonNull Hub> cache;
    private final int timeout;

    public HubMemo(TaskExecutor executor, SocketIOService socketIOService, Supplier<History> makeHistory, int timeout) {
        this.executor = executor;
        this.socketIOService = socketIOService;
        this.makeHistory = makeHistory;
        this.cache = CacheBuilder.newBuilder()
            .build(new CacheLoader<>() {
                public Hub load(String key) {
                    return HubMemo.this.compute(key);
                }
            });
        this.timeout = timeout;
    }

    public Map<String, Hub> all() {
        return cache.asMap();
    }

    public List<Hub> hubs() {
        return cache.asMap().values().stream().toList();
    }

    public Hub get(String gameId) {
        return cache.getUnchecked(gameId);
    }

    public Optional<Hub> getIfPresent(String gameId) {
        return Optional.ofNullable(cache.getIfPresent(gameId));
    }

    public Hub getFromFullId(String fullId) {
        return get(DbGame.takeGameId(fullId));
    }

    public Optional<Hub> getIfPresentFromFullId(String fullId) {
        return getIfPresent(DbGame.takeGameId(fullId));
    }

    public void remove(String gameId) {
        cache.invalidate(gameId);
    }

    public long count() {
        return cache.size();
    }

    private Hub compute(String gameId) {
        return new Hub(executor, socketIOService, gameId, makeHistory.get(), timeout);
    }
}

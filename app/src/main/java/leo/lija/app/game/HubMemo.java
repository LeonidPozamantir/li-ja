package leo.lija.app.game;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import leo.lija.app.config.SocketIOService;
import leo.lija.app.entities.DbGame;
import lombok.NonNull;
import org.springframework.core.task.TaskExecutor;

import java.util.Map;
import java.util.function.Supplier;

public class HubMemo {

    private final TaskExecutor executor;

    private final SocketIOService socketIOService;
    private final Supplier<History> makeHistory;
    private final LoadingCache<@NonNull String, @NonNull Hub> cache;

    public HubMemo(TaskExecutor executor, SocketIOService socketIOService, Supplier<History> makeHistory) {
        this.executor = executor;
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
        return new Hub(executor, socketIOService, gameId, makeHistory.get());
    }
}

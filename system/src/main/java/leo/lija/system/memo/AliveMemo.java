package leo.lija.system.memo;

import com.google.common.cache.Cache;
import jakarta.annotation.PostConstruct;
import leo.lija.chess.Color;
import leo.lija.system.config.MemoConfig;
import leo.lija.system.entities.DbGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AliveMemo {

    private final MemoConfig config;

    private Cache<String, Long> cache;

    private static final int BIG_LATENCY = 9999;

    @PostConstruct
    void init() {
        cache = Builder.expiry(config.alive().hardTimeout());
    }

    public Optional<Long> get(String gameId, Color color) {
        return Optional.ofNullable(cache.getIfPresent(toKey(gameId, color)));
    }

    public void put(String key) {
        cache.put(key, now());
    }

    public void put(String gameId, Color color) {
        cache.put(toKey(gameId, color), now());
    }

    public void put(String gameId, Color color, Long time) {
        cache.put(toKey(gameId, color), time);
    }

    public void transfer(String g1, Color c1, String g2, Color c2) {
        get(g1, c1).ifPresent(t -> put(g2, c2, t));
    }

    public int latency(String gameId, Color color) {
        return get(gameId, color).map(time -> (int)(now() - time)).orElse(BIG_LATENCY);
    }

    public int activity(DbGame game, Color color) {
        if (game.player(color).isAi()) return 2;
        return activity(game.getId(), color);
    }

    public int activity(String gameId, Color color) {
        int l = latency(gameId, color);
        if (l <= config.alive().softTimeout()) return 2;
        if (l <= config.alive().hardTimeout()) return 1;
        return 0;
    }

    public long count() {
        return cache.size();
    }

    public String toKey(String gameId, Color color) {
        return gameId + "." + color.getLetter();
    }

    private long now() {
        return System.currentTimeMillis();
    }
}

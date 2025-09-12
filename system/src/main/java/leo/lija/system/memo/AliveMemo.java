package leo.lija.system.memo;

import com.google.common.cache.Cache;
import leo.lija.chess.Color;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AliveMemo {

    @Value("${memo.alive.hard-timeout}")
    int hardTimeout;
    @Value("${memo.alive.soft-timeout}")
    int softTimeout;

    private Cache<String, Long> cache = Builder.expiry(hardTimeout);

    private static int BIG_LATENCY = 9999;

    public Optional<Long> get(String gameId, Color color) {
        return Optional.ofNullable(cache.getIfPresent(toKey(gameId, color)));
    }

    public void put(String gameId, Color color) {
        put(gameId, color, now());
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

    public int activity(DbGame game, DbPlayer player) {
        if (player.isAi()) return 2;
        else {
            int l = latency(game.getId(), player.getColor());
            if (l <= softTimeout) return 2;
            if (l <= hardTimeout) return 1;
            return 0;
        }
    }

    public long count() {
        return cache.size();
    }

    private String toKey(String gameId, Color color) {
        return gameId + "." + color.getLetter();
    }

    private long now() {
        return System.currentTimeMillis();
    }
}

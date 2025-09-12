package leo.lija.system.memo;

import com.google.common.cache.LoadingCache;
import leo.lija.chess.Color;
import leo.lija.system.GameRepo;
import leo.lija.system.entities.DbGame;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;

@Service
@RequiredArgsConstructor
public class VersionMemo {

    private final GameRepo repo;

    @Value("${memo.version.timeout}")
    private int timeout;

    private LoadingCache<String, Integer> cache = Builder.cache(timeout, this::compute);

    public Integer get(String gameId, Color color) {
        return cache.getUnchecked(toKey(gameId, color));
    }

    public void put(String gameId, Color color, Integer version) {
        cache.put(toKey(gameId, color), version);
    }

    public void put(DbGame game) {
        put(game.getId(), WHITE, game.player(WHITE).eventStack().lastVersion());
        put(game.getId(), BLACK, game.player(BLACK).eventStack().lastVersion());
    }

    private String toKey(String gameId, Color color) {
        return gameId + "." + color.getLetter();
    }

    private Integer compute(String key) {
        String[] s = key.split("\\.");
        if (s.length != 2) return 0;
        String letter = s[1];
        if (letter.isEmpty()) return 0;
        String gameId = s[0];

        return Color.apply(letter.substring(0, 1))
            .map(color -> {
                try {
                    return repo.playerOnly(gameId, color).eventStack().lastVersion();
                } catch (Exception e) {
                    return 0;
                }
            }).orElse(0);
    }
}

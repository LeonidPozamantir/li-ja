package leo.lija.app.memo;

import com.google.common.cache.LoadingCache;
import jakarta.annotation.PostConstruct;
import leo.lija.chess.Color;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Pov;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;

@RequiredArgsConstructor
public class VersionMemo {

    private final BiFunction<String, Color, Pov> getPov;
    private final int timeout;

    private LoadingCache<String, Integer> cache;

    @PostConstruct
    void init() {
        cache = Builder.cache(timeout, this::compute);
    }

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
                    return getPov.apply(gameId, color).player().eventStack().lastVersion();
                } catch (Exception e) {
                    return 0;
                }
            }).orElse(0);
    }
}

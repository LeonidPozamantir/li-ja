package leo.lija.system.memo;

import com.google.common.cache.LoadingCache;
import leo.lija.chess.Color;
import leo.lija.chess.utils.Pair;
import leo.lija.system.GameRepo;
import leo.lija.system.entities.DbGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;

@Service
@RequiredArgsConstructor
public class VersionMemo {

    private final GameRepo repo;

    private LoadingCache<Pair<String, Boolean>, Integer> cache = Builder.cache(1800, this::compute);

    public Integer get(String gameId, Color color) {
        return cache.getUnchecked(Pair.of(gameId, color == WHITE));
    }

    public void put(String gameId, Color color, Integer version) {
        cache.put(Pair.of(gameId, color == WHITE), version);
    }

    public void put(DbGame game) {
        put(game.getId(), WHITE, game.player(WHITE).eventStack().lastVersion());
        put(game.getId(), BLACK, game.player(BLACK).eventStack().lastVersion());
    }

    private Integer compute(Pair<String, Boolean> pair) {
        String gameId = pair.getFirst();
        boolean isWhite = pair.getSecond();
        try {
            return repo.playerOnly(gameId, Color.apply(isWhite)).eventStack().lastVersion();
        } catch (Exception e) {
            return 0;
        }
    }
}

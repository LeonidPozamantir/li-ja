package leo.lija.system.memo;

import leo.lija.chess.Color;
import leo.lija.chess.utils.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class VersionCache {

    private Function<String, Integer> memo = Builder.cache(1800, this::compute);


    private String toKey(String gameId, Color color) {
        return gameId + ":" + color.getLetter() + ":v";
    }

    private Optional<Pair<String, Color>> fromKey(String key) {
        String[] list = key.split(":");
        if (list.length != 3 || !list[2].equals("v")) return Optional.empty();
        String gameId = list[0];
        String cName = list[1];
        return Color.apply(cName).map(c -> Pair.of(gameId, c));
    }

    private int compute(String key) {
        return fromKey(key)
            .map(pair -> compute(pair.getFirst(), pair.getSecond()))
            .orElse(0);
    }

    private int compute(String gameId, Color color) {
        return 33;
    }

}

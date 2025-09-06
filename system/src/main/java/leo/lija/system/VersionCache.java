package leo.lija.system;

import leo.lija.chess.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VersionCache {

    private final RedisService redis;

    public Optional<Integer> get(String gameId, Color color) {
        return redis.getInt(key(gameId, color));
    }

    private String key(String gameId, Color color) {
        return gameId + ":" + color.getLetter() + ":v";
    }

}

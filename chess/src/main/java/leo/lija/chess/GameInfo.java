package leo.lija.chess;

import java.util.Map;
import java.util.Optional;

public record GameInfo(Game game, String pgn, Optional<Opening> opening ) {

    public GameInfo(Game game, String pgn) {
        this(game, pgn, Optional.empty());
    }

    public Map<String, Object> toMap() {
        return Map.of(
            "pgn", pgn,
            "opening", opening.map(o -> Map.of(
                "code", o.code(),
                "name", o.name()
            ))
        );
    }
}

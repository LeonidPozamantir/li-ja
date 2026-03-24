package leo.lija.app;

import leo.lija.app.entities.DbGame;
import leo.lija.chess.Opening;

import java.util.Map;
import java.util.Optional;

public record GameInfo(DbGame game, String pgn, String fen, Optional<Opening> opening) {

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

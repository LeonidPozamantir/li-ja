package leo.lija.system;

import leo.lija.chess.Situation;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record GameStatus(Integer id, String name) {

    private Integer toInt() {
        return this.id;
    }

    private static final Set<GameStatus> values = Set.of(
        new GameStatus(10, "created"),
        new GameStatus(20, "started"),
        new GameStatus(25, "aborted"),
        new GameStatus(30, "mate"),
        new GameStatus(31, "resign"),
        new GameStatus(32, "stalemate"),
        new GameStatus(33, "timeout"),
        new GameStatus(34, "draw"),
        new GameStatus(35, "outoftime"),
        new GameStatus(36, "cheat")
    );

    private static final Map<String, GameStatus> allByName = Collections.unmodifiableMap(
        values.stream().collect(Collectors.toMap(GameStatus::name, Function.identity()))
    );

    private static Optional<GameStatus> find(String name) {
        return Optional.ofNullable(allByName.get(name));
    }

    public static Optional<Integer> fromSituation(Situation situation) {
        Optional<GameStatus> gs;
        if (situation.checkmate()) gs = find("checkmate");
        else if (situation.stalemate()) gs = find("stalemate");
        else if (situation.autoDraw()) gs = find("draw");
        else gs = Optional.empty();
        return gs.map(GameStatus::toInt);
    }
}

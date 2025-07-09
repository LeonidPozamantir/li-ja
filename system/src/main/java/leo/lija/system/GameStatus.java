package leo.lija.system;

import java.util.Set;

public record GameStatus(int id, String name) {
    public static final Set<GameStatus> values = Set.of(
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
}

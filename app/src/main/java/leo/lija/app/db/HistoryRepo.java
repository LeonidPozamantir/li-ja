package leo.lija.app.db;

import leo.lija.app.entities.DbHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HistoryRepo {

    private final HistoryRepoJpa jpa;

    public static final int TYPE_START = 1;
    public static final int TYPE_GAME = 2;
    public static final int TYPE_ADJUST = 3;

    public void addEntry(String username, int elo, Optional<String> gameId) {
        addEntry(username, elo, gameId, TYPE_GAME);
    }

    public void addEntry(String username, int elo, int entryType) {
        addEntry(username, elo, Optional.empty(), entryType);
    }

    public void addEntry(String username, int elo, Optional<String> gameId, int entryType) {
        String tsKey = String.valueOf(System.currentTimeMillis() / 1000);
        jpa.save(new DbHistory(
            new DbHistory.DbHistoryKey(username, tsKey),
            entryType,
            elo,
            gameId.orElse(null)
        ));
    }
}

package leo.lija.system.db;

import leo.lija.system.entities.DbHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HistoryRepo {

    private final HistoryRepoJpa jpa;

    private static final int ENTRY_TYPE = 2;

    public void addEntry(String username, int elo, String gameId) {
        String tsKey = String.valueOf(System.currentTimeMillis() / 1000);
        jpa.save(new DbHistory(
            new DbHistory.DbHistoryKey(username, tsKey),
            ENTRY_TYPE,
            elo,
            gameId
        ));
    }
}

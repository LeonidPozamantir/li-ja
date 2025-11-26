package leo.lija.app;

import leo.lija.app.db.HistoryRepo;
import leo.lija.app.db.UserRepo;
import leo.lija.app.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EloUpdater {

    private final UserRepo userRepo;
    private final HistoryRepo historyRepo;

    public void game(User user, int elo, String gameId) {
        userRepo.setElo(user.getId(), elo);
        historyRepo.addEntry(user.getUsernameCanonical(), elo, Optional.of(gameId));
    }

    public void adjust(User user, int elo) {
        userRepo.setElo(user.getId(), elo);
        historyRepo.addEntry(user.getUsernameCanonical(), elo, HistoryRepo.TYPE_ADJUST);
    }
}

package leo.lija.app.command;

import leo.lija.app.Finisher;
import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.DbGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameFinish {

    private final GameRepo gameRepo;
    private final Finisher finisher;

    public void apply() {
        List<DbGame> games = gameRepo.candidateToAutofinish();
        finisher.outoftimes(games);
    }
}

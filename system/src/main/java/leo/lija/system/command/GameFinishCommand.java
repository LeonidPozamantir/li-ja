package leo.lija.system.command;

import leo.lija.system.Finisher;
import leo.lija.system.db.GameRepo;
import leo.lija.system.entities.DbGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameFinishCommand {

    private final GameRepo gameRepo;
    private final Finisher finisher;

    public void apply() {
        List<DbGame> games = gameRepo.candidateToAutofinish();
        System.out.println("[cron] finish %d games (%s)".formatted(games.size(), games.subList(0, Math.min(3, games.size())).stream().map(DbGame::getId).collect(Collectors.joining(", "))));
        finisher.outoftimes(games);
    }
}

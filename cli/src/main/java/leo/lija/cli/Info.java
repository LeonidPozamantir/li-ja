package leo.lija.cli;

import leo.lija.system.db.GameRepoJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Info implements Command {

    private final GameRepoJpa gameRepo;

    @Override
    public void apply() {
        long nb = nbGames();
        System.out.println("%d games in DB".formatted(nb));
    }

    private long nbGames() {
        return gameRepo.count();
    }
}

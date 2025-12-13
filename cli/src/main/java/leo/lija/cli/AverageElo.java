package leo.lija.cli;

import leo.lija.app.db.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AverageElo implements Command {

    private final UserRepo userRepo;

    @Override
    public void apply() {
        int avg = userRepo.averageElo();
        System.out.printf("Average elo is %d", avg);
    }
}

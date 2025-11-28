package leo.lija.app.command;

import leo.lija.app.db.GameRepoJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GameCleanNext {

    private final GameRepoJpa jpa;

    public void apply() {
        jpa.cleanNext(LocalDateTime.now().minusDays(3));
    }
}

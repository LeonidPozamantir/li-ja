package leo.lija.system;

import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.event.EndEvent;
import leo.lija.system.entities.event.MessageEvent;
import leo.lija.system.memo.VersionMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternalApi {

    private final GameRepo repo;
    private final VersionMemo versionMemo;

    public void talk(String gameId, String author, String message) {
        DbGame g1 = repo.game(gameId);
        g1.withEvents(List.of(new MessageEvent(author, message)));
        repo.save(g1);
        versionMemo.put(g1);
    }

    public void endGame(String gameId) {
        DbGame g1 = repo.game(gameId);
        g1.withEvents(List.of(new EndEvent()));
        repo.save(g1);
        versionMemo.put(g1);
    }

    public void updateVersion(String gameId) {
        versionMemo.put(repo.game(gameId));
    }

}

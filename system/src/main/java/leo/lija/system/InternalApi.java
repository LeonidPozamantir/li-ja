package leo.lija.system;

import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.event.EndEvent;
import leo.lija.system.entities.event.Event;
import leo.lija.system.entities.event.MessageEvent;
import leo.lija.system.entities.event.RedirectEvent;
import leo.lija.system.memo.VersionMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternalApi {

    private final GameRepo repo;
    private final VersionMemo versionMemo;

    public void join(String fullId, String url, String messages) {
        Pair<DbGame, DbPlayer> gameAndPlayer = repo.player(fullId);
        DbGame g1 = gameAndPlayer.getFirst();
        DbPlayer player = gameAndPlayer.getSecond();
        List<String> messageList = List.of(messages.split("\\$"));
        g1.withEvents(messageList.stream().map(m -> (Event) new MessageEvent("system", m)).toList());
        g1.withEvents(g1.opponent(player).getColor(), List.of(new RedirectEvent(url)));
        repo.save(g1);
        versionMemo.put(g1);
    }

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

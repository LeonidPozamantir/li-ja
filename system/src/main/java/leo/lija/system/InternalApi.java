package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.event.EndEvent;
import leo.lija.system.entities.event.Event;
import leo.lija.system.entities.event.MessageEvent;
import leo.lija.system.entities.event.RedirectEvent;
import leo.lija.system.entities.event.ReloadTableEvent;
import leo.lija.system.exceptions.AppException;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.VersionMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternalApi {

    private final GameRepo repo;
    private final VersionMemo versionMemo;
    private final AliveMemo aliveMemo;

    public void join(String fullId, String url, String messages) {
        Pair<DbGame, DbPlayer> gameAndPlayer = repo.player(fullId);
        DbGame g1 = gameAndPlayer.getFirst();
        DbPlayer player = gameAndPlayer.getSecond();
        g1.withEvents(decodeMessages(messages));
        g1.withEvents(g1.opponent(player).getColor(), List.of(new RedirectEvent(url)));
        save(g1);
    }

    public void talk(String gameId, String author, String message) {
        DbGame g1 = repo.game(gameId);
        g1.withEvents(List.of(new MessageEvent(author, message)));
        save(g1);
    }

    public void end(String gameId, String messages) {
        DbGame g1 = repo.game(gameId);
        ArrayList<Event> newEvents = new ArrayList<>(List.of(new EndEvent()));
        newEvents.addAll(decodeMessages(messages));
        g1.withEvents(newEvents);
        save(g1);
    }

    public void acceptRematch(String gameId, String newGameId, String colorName, String whiteRedirect, String blackRedirect) {
        Color color = ioColor(colorName);
        DbGame g1 = repo.game(gameId);
        g1.withEvents(
            List.of(new RedirectEvent(whiteRedirect)),
            List.of(new RedirectEvent(blackRedirect))
        );
        save(g1);
        aliveMemo.put(newGameId, color.getOpposite());
        aliveMemo.transfer(gameId, color.getOpposite(), newGameId, color);
    }

    public void updateVersion(String gameId) {
        versionMemo.put(repo.game(gameId));
    }

    public void alive(String gameId, String colorName) {
        Color color = ioColor(colorName);
        aliveMemo.put(gameId, color);
    }

    private Color ioColor(String colorName) {
        return Color.apply(colorName).orElseThrow(() -> new AppException("Invalid color"));
    }

    public void reloadTable(String gameId) {
        DbGame g1 = repo.game(gameId);
        g1.withEvents(List.of(new ReloadTableEvent()));
        save(g1);
    }

    private void save(DbGame g1) {
        repo.save(g1);
        versionMemo.put(g1);
    }

    private List<Event> decodeMessages(String messages) {
        return Arrays.stream(messages.split("\\$")).map(m -> (Event) new MessageEvent("system", m)).toList();
    }
}

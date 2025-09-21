package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.chess.utils.Pair;
import leo.lija.system.db.GameRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.entry.EntryGame;
import leo.lija.system.entities.event.EndEvent;
import leo.lija.system.entities.event.Event;
import leo.lija.system.entities.event.MessageEvent;
import leo.lija.system.entities.event.RedirectEvent;
import leo.lija.system.entities.event.ReloadTableEvent;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.VersionMemo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AppApi extends IOTools {

    AppApi(GameRepo gameRepo, VersionMemo versionMemo, AliveMemo aliveMemo, LobbyApi lobbyApi) {
        super(gameRepo, versionMemo);
        this.aliveMemo = aliveMemo;
        this.lobbyApi = lobbyApi;
    }

    private final AliveMemo aliveMemo;
    private final LobbyApi lobbyApi;

    public void join(String fullId, String url, String messages, EntryGame entryGame) {
        Pair<DbGame, DbPlayer> gameAndPlayer = gameRepo.player(fullId);
        DbGame g1 = gameAndPlayer.getFirst();
        DbPlayer player = gameAndPlayer.getSecond();
        g1.withEvents(decodeMessages(messages));
        g1.withEvents(g1.opponent(player).getColor(), List.of(new RedirectEvent(url)));
        save(g1);
        lobbyApi.addEntry(entryGame);
    }

    public void talk(String gameId, String author, String message) {
        DbGame g1 = gameRepo.game(gameId);
        g1.withEvents(List.of(new MessageEvent(author, message)));
        save(g1);
    }

    public void end(String gameId, String messages) {
        DbGame g1 = gameRepo.game(gameId);
        ArrayList<Event> newEvents = new ArrayList<>(List.of(new EndEvent()));
        newEvents.addAll(decodeMessages(messages));
        g1.withEvents(newEvents);
        save(g1);
    }

    public void start(EntryGame entryGame) {
        lobbyApi.addEntry(entryGame);
    }


    public void acceptRematch(String gameId, String newGameId, String colorName, String whiteRedirect, String blackRedirect, EntryGame entryGame) {
        Color color = ioColor(colorName);
        DbGame g1 = gameRepo.game(gameId);
        g1.withEvents(
            List.of(new RedirectEvent(whiteRedirect)),
            List.of(new RedirectEvent(blackRedirect))
        );
        save(g1);
        aliveMemo.put(newGameId, color.getOpposite());
        aliveMemo.transfer(gameId, color.getOpposite(), newGameId, color);
        lobbyApi.addEntry(entryGame);
    }

    public void updateVersion(String gameId) {
        versionMemo.put(gameRepo.game(gameId));
    }

    public void alive(String gameId, String colorName) {
        Color color = ioColor(colorName);
        aliveMemo.put(gameId, color);
    }

    public void draw(String gameId, String colorName, String messages) {
        Color color = ioColor(colorName);
        DbGame g1 = gameRepo.game(gameId);
        g1.withEvents(decodeMessages(messages));
        g1.withEvents(color.getOpposite(), List.of(new ReloadTableEvent()));
        save(g1);
    }

    public void drawAccept(String gameId, String colorName, String messages) {
        Color color = ioColor(colorName);
        DbGame g1 = gameRepo.game(gameId);
        ArrayList<Event> newEvents = new ArrayList<>(List.of(new EndEvent()));
        newEvents.addAll(decodeMessages(messages));
        g1.withEvents(newEvents);
        save(g1);
    }

    public int activity(String gameId, String colorName) {
        return Color.apply(colorName).map(color -> aliveMemo.activity(gameId, color)).orElse(0);
    }


    public void reloadTable(String gameId) {
        DbGame g1 = gameRepo.game(gameId);
        g1.withEvents(List.of(new ReloadTableEvent()));
        save(g1);
    }

    private List<Event> decodeMessages(String messages) {
        return Arrays.stream(messages.split("\\$")).map(m -> (Event) new MessageEvent("system", m)).toList();
    }
}

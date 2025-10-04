package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.chess.Pos;
import leo.lija.system.db.GameRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.Pov;
import leo.lija.system.entities.Room;
import leo.lija.system.entities.event.RedirectEvent;
import leo.lija.system.entities.event.ReloadTableEvent;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.VersionMemo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppApi extends IOTools {

    AppApi(GameRepo gameRepo, VersionMemo versionMemo, AliveMemo aliveMemo, Messenger messenger, Starter starter) {
        super(gameRepo, versionMemo);
        this.aliveMemo = aliveMemo;
        this.messenger = messenger;
        this.starter = starter;
    }

    private final Messenger messenger;
    private final AliveMemo aliveMemo;
    private final Starter starter;

    public void join(String fullId, String url, String messages, String entryData) {
        Pov pov = gameRepo.pov(fullId);
        starter.start(pov.game(), entryData);
        messenger.systemMessages(pov.game(), messages);
        pov.game().withEvents(pov.color().getOpposite(), List.of(new RedirectEvent(url)));
        save(pov.game());
    }

    public void start(String gameId, String entryData) {
        DbGame g1 = gameRepo.game(gameId);
        starter.start(g1, entryData);
        save(g1);
    }

    public void rematchAccept(
            String gameId,
            String newGameId,
            String colorName,
            String whiteRedirect,
            String blackRedirect,
            String entryData,
            String messageString) {
        Color color = ioColor(colorName);
        DbGame g1 = gameRepo.game(gameId);
        DbGame newGame = gameRepo.game(newGameId);
        g1.withEvents(
            List.of(new RedirectEvent(whiteRedirect)),
            List.of(new RedirectEvent(blackRedirect))
        );
        save(g1);
        messenger.systemMessages(newGame, messageString);
        starter.start(newGame, entryData);
        save(newGame);
        aliveMemo.put(newGameId, color.getOpposite());
        aliveMemo.transfer(gameId, color.getOpposite(), newGameId, color);
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
        messenger.systemMessages(g1, messages);
        g1.withEvents(color.getOpposite(), List.of(new ReloadTableEvent()));
        save(g1);
    }

    public int activity(String gameId, String colorName) {
        return Color.apply(colorName).map(color -> aliveMemo.activity(gameId, color)).orElse(0);
    }

    public List<Room.RoomMessage> room(String gameId) {
        return messenger.render(gameId);
    }

    public void reloadTable(String gameId) {
        DbGame g1 = gameRepo.game(gameId);
        g1.withEvents(List.of(new ReloadTableEvent()));
        save(g1);
    }

    public Map<String, Object> possibleMoves(String gameId, String colorName) {
        Color color = ioColor(colorName);
        DbGame game = gameRepo.game(gameId);
        return game.toChess().situation().destinations().entrySet().stream()
            .collect(Collectors.toMap(e -> e.getKey().key(), e -> e.getValue().stream().map(Pos::toString).collect(Collectors.joining())));
    }
}

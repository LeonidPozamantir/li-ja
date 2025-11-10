package leo.lija.app;

import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Evented;
import leo.lija.app.entities.Pov;
import leo.lija.app.entities.Room;
import leo.lija.app.entities.event.Event;
import leo.lija.app.entities.event.RedirectEvent;
import leo.lija.app.entities.event.ReloadTableEvent;
import leo.lija.app.game.HubMemo;
import leo.lija.app.memo.AliveMemo;
import leo.lija.chess.Color;
import leo.lija.chess.Pos;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;

@Service
public class AppApi extends IOTools {

    AppApi(GameRepo gameRepo, AliveMemo aliveMemo, HubMemo gameHubMemo, Messenger messenger, Starter starter) {
        super(gameRepo);
        this.aliveMemo = aliveMemo;
        this.gameHubMemo = gameHubMemo;
        this.messenger = messenger;
        this.starter = starter;
    }

    private final AliveMemo aliveMemo;
    private final HubMemo gameHubMemo;
    private final Messenger messenger;
    private final Starter starter;

    public Map<String, Object> show(String fullId) {
        int version = gameHubMemo.getFromFullId(fullId).getVersion();
        Pov pov = gameRepo.pov(fullId);
        aliveMemo.put(pov.game().getId(), pov.color());
        List<Room.RoomMessage> roomData = messenger.render(pov.game().getId());
        Map<String, Object> res = new HashMap<>(Map.of(
            "version", version,
            "roomData", roomData,
            "opponentActivity", aliveMemo.activity(pov.game().getId(), pov.color().getOpposite())
        ));
        if (pov.game().playableBy(pov.player())) {
            res.put("possibleMoves", pov.game().toChess().situation().destinations().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().key(), e -> e.getValue().stream().map(Pos::toString).collect(Collectors.joining()))));
        }
        return res;
    }

    public void join(String fullId, String url, String messages, String entryData) {
        Pov pov = gameRepo.pov(fullId);
        Evented e1 = starter.start(pov.game(), entryData);
        e1.add(new RedirectEvent(pov.color().getOpposite(), url));
        e1.addAll(messenger.systemMessages(e1.game(), messages));
        save(e1);
    }

    public void start(String gameId, String entryData) {
        DbGame g1 = gameRepo.game(gameId);
        Evented evented = starter.start(g1, entryData);
        save(evented);
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
        DbGame newGame = gameRepo.game(newGameId);
        DbGame g1 = gameRepo.game(gameId);
        Evented evented = new Evented(g1, List.of(
            new RedirectEvent(WHITE, whiteRedirect),
            new RedirectEvent(BLACK, blackRedirect),
            // to tell spectators to reload the table
            new ReloadTableEvent(WHITE),
            new ReloadTableEvent(BLACK)
        ));
        save(evented);
        Evented newEvented = starter.start(newGame, entryData);
        newEvented.addAll(messenger.systemMessages(newEvented.game(), messageString));
        save(newEvented);
        aliveMemo.put(newGameId, color.getOpposite());
        aliveMemo.transfer(gameId, color.getOpposite(), newGameId, color);
    }

    public void reloadTable(String gameId) {
        DbGame g1 = gameRepo.game(gameId);
        Evented evented = new Evented(g1, Color.all.stream().map(c -> (Event) new ReloadTableEvent(c)).toList());
        save(evented);
    }

    public void alive(String gameId, String colorName) {
        Color color = ioColor(colorName);
        aliveMemo.put(gameId, color);
    }

    public int gameVersion(String gameId) {
        return gameHubMemo.get(gameId).getVersion();
    }

    public int activity(String gameId, String colorName) {
        return Color.apply(colorName).map(color -> aliveMemo.activity(gameId, color)).orElse(0);
    }

}

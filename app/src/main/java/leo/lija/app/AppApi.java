package leo.lija.app;

import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Pov;
import leo.lija.app.entities.Progress;
import leo.lija.app.entities.Room;
import leo.lija.app.entities.event.Event;
import leo.lija.app.entities.event.RedirectEvent;
import leo.lija.app.entities.event.ReloadTableEvent;
import leo.lija.app.exceptions.AppException;
import leo.lija.app.game.HubMemo;
import leo.lija.app.game.Socket;
import leo.lija.chess.Color;
import leo.lija.chess.Pos;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;

@Service
public class AppApi {

    private final GameRepo gameRepo;
    private final Socket gameSocket;
    private final HubMemo gameHubMemo;
    private final Messenger messenger;
    private final Starter starter;

    AppApi(GameRepo gameRepo, @Qualifier("gameSocket") Socket gameSocket, HubMemo gameHubMemo, Messenger messenger, Starter starter) {
        this.gameRepo = gameRepo;
        this.gameSocket = gameSocket;
        this.gameHubMemo = gameHubMemo;
        this.messenger = messenger;
        this.starter = starter;
    }

    public Map<String, Object> show(String fullId) {
        int version = gameHubMemo.getFromFullId(fullId).getVersion();
        Pov pov = gameRepo.pov(fullId);
        List<Room.RoomMessage> roomData = messenger.render(pov.game().getId());
        Map<String, Object> res = new HashMap<>(Map.of(
            "version", version,
            "roomData", roomData
        ));
        if (pov.game().playableBy(pov.player())) {
            res.put("possibleMoves", pov.game().toChess().situation().destinations().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().key(), e -> e.getValue().stream().map(Pos::toString).collect(Collectors.joining()))));
        }
        return res;
    }

    public void join(String fullId, String url, String messages, String entryData) {
        Pov pov = gameRepo.pov(fullId);
        Progress p1 = starter.start(pov.game(), entryData);
        p1.add(new RedirectEvent(pov.color().getOpposite(), url));
        p1.addAll(messenger.systemMessages(p1.game(), messages));
        gameRepo.save(p1);
        gameSocket.send(p1);
    }

    public void start(String gameId, String entryData) {
        DbGame g1 = gameRepo.game(gameId);
        Progress progress = starter.start(g1, entryData);
        gameRepo.save(progress);
        gameSocket.send(progress);
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
        Progress progress = new Progress(g1, List.of(
            new RedirectEvent(WHITE, whiteRedirect),
            new RedirectEvent(BLACK, blackRedirect),
            // to tell spectators to reload the table
            new ReloadTableEvent(WHITE),
            new ReloadTableEvent(BLACK)
        ));
        gameRepo.save(progress);
        gameSocket.send(progress);
        Progress newProgress = starter.start(newGame, entryData);
        newProgress.addAll(messenger.systemMessages(newProgress.game(), messageString));
        gameRepo.save(newProgress);
        gameSocket.send(newProgress);
    }

    public void reloadTable(String gameId) {
        DbGame g1 = gameRepo.game(gameId);
        Progress progress = new Progress(g1, Color.all.stream().map(c -> (Event) new ReloadTableEvent(c)).toList());
        gameRepo.save(progress);
        gameSocket.send(progress);
    }

    public int gameVersion(String gameId) {
        return gameHubMemo.get(gameId).getVersion();
    }

    public boolean isConnected(String gameId, String colorName) {
        return Color.apply(colorName).map(c ->
            gameHubMemo.get(gameId).isConnected(c)
        ).orElse(false);
    }

    private Color ioColor(String colorName) {
        return Color.apply(colorName).orElseThrow(() -> new AppException("Invalid color"));
    }

}

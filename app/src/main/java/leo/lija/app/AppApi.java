package leo.lija.app;

import leo.lija.app.db.GameRepo;
import leo.lija.app.db.UserRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Pov;
import leo.lija.app.entities.Progress;
import leo.lija.app.entities.Room;
import leo.lija.app.entities.User;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;

@Service
public class AppApi {

    private final UserRepo userRepo;
    private final GameRepo gameRepo;
    private final Socket gameSocket;
    private final HubMemo gameHubMemo;
    private final Messenger messenger;
    private final Starter starter;
    private final EloUpdater eloUpdater;

    AppApi(UserRepo userRepo, GameRepo gameRepo, @Qualifier("gameSocket") Socket gameSocket, HubMemo gameHubMemo, Messenger messenger, Starter starter, EloUpdater eloUpdater) {
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.gameSocket = gameSocket;
        this.gameHubMemo = gameHubMemo;
        this.messenger = messenger;
        this.starter = starter;
        this.eloUpdater = eloUpdater;
    }

    public Map<String, Object> show(String fullId) {
        int version = gameHubMemo.getFromFullId(fullId).getVersion();
        Optional<Pov> povOption = gameRepo.pov(fullId);
        return povOption.map(pov -> {
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
        }).orElseThrow(Utils::gameNotFound);
    }

    public void join(String fullId, String url, String messages, String entryData) {
        Optional<Pov> povOption = gameRepo.pov(fullId);
        povOption.ifPresentOrElse(pov -> {
            Progress p1 = starter.start(pov.game(), entryData);
            p1.add(new RedirectEvent(pov.color().getOpposite(), url));
            p1.addAll(messenger.systemMessages(p1.game(), messages));
            gameRepo.save(p1);
            gameSocket.send(p1);
        }, () -> {
            throw Utils.gameNotFound();
        });
    }

    public void start(String gameId, String entryData) {
        gameRepo.game(gameId).ifPresentOrElse(g1 -> {
            Progress progress = starter.start(g1, entryData);
            gameRepo.save(progress);
            gameSocket.send(progress);
        }, () -> {
            throw new AppException("No such game");
        });
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
        Optional<DbGame> newGameOption = gameRepo.game(newGameId);
        Optional<DbGame> g1Option = gameRepo.game(gameId);
        newGameOption.ifPresentOrElse(newGame ->
            g1Option.ifPresentOrElse(g1 -> {
                Progress progress = new Progress(g1, List.of(
                    new RedirectEvent(WHITE, whiteRedirect),
                    new RedirectEvent(BLACK, blackRedirect),
                    // tell spectators to reload the table
                    new ReloadTableEvent(WHITE),
                    new ReloadTableEvent(BLACK)
                ));
                gameRepo.save(progress);
                gameSocket.send(progress);
                Progress newProgress = starter.start(newGame, entryData);
                newProgress.addAll(messenger.systemMessages(newProgress.game(), messageString));
                gameRepo.save(newProgress);
                gameSocket.send(newProgress);
                }, () -> {
                    throw Utils.gameNotFound();
                }
            ), () -> {
                throw Utils.gameNotFound();
            }
        );
    }

    public void reloadTable(String gameId) {
        Optional<DbGame> g1Option = gameRepo.game(gameId);
        g1Option.ifPresentOrElse(g1 -> {
            Progress progress = new Progress(g1, Color.all.stream().map(c -> (Event) new ReloadTableEvent(c)).toList());
            gameRepo.save(progress);
            gameSocket.send(progress);
        }, () -> {
            throw Utils.gameNotFound();
        });
    }

    public int gameVersion(String gameId) {
        return gameHubMemo.get(gameId).getVersion();
    }

    public boolean isConnected(String gameId, String colorName) {
        return Color.apply(colorName).map(c ->
            gameHubMemo.get(gameId).isConnected(c)
        ).orElse(false);
    }

    public void adjust(String username) {
        Optional<User> userOption = userRepo.user(username);
        userOption.ifPresent(user -> {
            if (user.getElo() > User.STARTING_ELO) eloUpdater.adjust(user, User.STARTING_ELO);
            userRepo.setEngine(user.getId());
        });
    }

    private Color ioColor(String colorName) {
        return Color.apply(colorName).orElseThrow(() -> new AppException("Invalid color"));
    }

}

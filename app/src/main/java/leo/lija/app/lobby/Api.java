package leo.lija.app.lobby;

import leo.lija.app.Messenger;
import leo.lija.app.Starter;
import leo.lija.app.Utils;
import leo.lija.app.db.GameRepo;
import leo.lija.app.db.HookRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Hook;
import leo.lija.app.entities.Progress;
import leo.lija.app.exceptions.AppException;
import leo.lija.chess.Color;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Api {

    private final HookRepo hookRepo;
    private final Fisherman fisherman;
    private final GameRepo gameRepo;
    private final leo.lija.app.game.Socket gameSocket;
    private final Messenger messenger;
    private final Starter starter;

    public Api(HookRepo hookRepo, Fisherman fisherman, GameRepo gameRepo, @Qualifier("gameSocket") leo.lija.app.game.Socket gameSocket, Messenger messenger, Starter starter) {
        this.hookRepo = hookRepo;
        this.fisherman = fisherman;
        this.gameRepo = gameRepo;
        this.gameSocket = gameSocket;
        this.messenger = messenger;
        this.starter = starter;
    }


    public void cancel(String ownerId) {
        Optional<Hook> hook = hookRepo.findByOwnerId(ownerId);
        hook.ifPresent(fisherman::delete);
    }

    public void join(
            String gameId,
            String colorName,
            String entryData,
            String messageString,
            String hookOwnerId,
            Optional<String> myHookOwnerId
    ) {
        Optional<Hook> hook = hookRepo.findByOwnerId(hookOwnerId);
        Color color = ioColor(colorName);
        Optional<DbGame> gameOption = gameRepo.game(gameId);
        gameOption.ifPresentOrElse(game -> {
            Progress p1 = starter.start(game, entryData);
            p1.addAll(messenger.systemMessages(game, messageString));
            gameRepo.save(p1);
            gameSocket.send(p1);
            hook.ifPresent(h -> fisherman.bite(h, p1.game()));
            myHookOwnerId.ifPresent(ownerId -> hookRepo.findByOwnerId(ownerId)
                .ifPresent(fisherman::delete));
        }, () -> {
            throw Utils.gameNotFound();
        });
    }

    public void create(String hookOwnerId) {
        Optional<Hook> hook = hookRepo.findByOwnerId(hookOwnerId);
        hook.ifPresent(fisherman::add);
    }

    private Color ioColor(String colorName) {
        return Color.apply(colorName).orElseThrow(() -> new AppException("Invalid color"));
    }

}

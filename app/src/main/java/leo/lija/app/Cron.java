package leo.lija.app;

import leo.lija.app.ai.RemoteAi;
import leo.lija.app.command.GameFinishCommand;
import leo.lija.app.db.GameRepo;
import leo.lija.app.db.HookRepo;
import leo.lija.app.db.UserRepo;
import leo.lija.app.lobby.Fisherman;
import leo.lija.app.lobby.Hub;
import leo.lija.app.memo.HookMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnWebApplication
@ConditionalOnBooleanProperty(name = "ai.server", havingValue = false)
@RequiredArgsConstructor
public class Cron {

    private final UserRepo userRepo;
    private final HookRepo hookRepo;
    private final GameRepo gameRepo;
    private final GameFinishCommand gameFinishCommand;
    private final RemoteAi remoteAi;
    private final Fisherman lobbyFisherman;
    private final Hub lobbyHub;
    private final TaskExecutor actionsExecutor;
    private final HookMemo hookMemo;

    private final int TIMEOUT = 200;

    @Scheduled(fixedRateString = "1s")
    void hookTick() {
        CompletableFuture.runAsync(() -> lobbyHub.withHooks(hookMemo::putAll), actionsExecutor)
            .orTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Scheduled(fixedRateString = "2s")
    void heartBeat() {
        lobbyHub.nbPlayers();
    }

    @Scheduled(fixedRateString = "2s")
    void hookCleanupDead() {
        lobbyFisherman.cleanup();
    }

    @Scheduled(fixedRateString = "21s")
    void hookCleanupOld() {
        hookRepo.cleanupOld();
    }

    @Scheduled(fixedRateString = "3s")
    void onlineUsername() {
        CompletableFuture.runAsync(() -> lobbyHub.withUsernames(userRepo::updateOnlineUserNames), actionsExecutor)
            .orTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Scheduled(fixedRateString = "2h")
    void gameCleanupUnplayed() {
        System.out.println("[cron] remove old unplayed games");
        gameRepo.cleanupUnplayed();
    }

    @Scheduled(fixedRateString = "1h")
    void gameAutoFinish() {
        gameFinishCommand.apply();
    }

    @Scheduled(fixedRateString = "10s")
    void remoteAiHealth() {
        remoteAi.diagnose();
    }
}

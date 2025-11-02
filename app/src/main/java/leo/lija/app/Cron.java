package leo.lija.app;

import leo.lija.app.ai.AiService;
import leo.lija.app.ai.RemoteAi;
import leo.lija.app.command.GameFinishCommand;
import leo.lija.app.db.GameRepo;
import leo.lija.app.db.HookRepo;
import leo.lija.app.db.UserRepo;
import leo.lija.app.lobby.Fisherman;
import leo.lija.app.lobby.Hub;
import leo.lija.app.memo.HookMemo;
import leo.lija.app.memo.UsernameMemo;
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
    private final UsernameMemo usernameMemo;
    private final HookRepo hookRepo;
    private final GameRepo gameRepo;
    private final GameFinishCommand gameFinishCommand;
    private final RemoteAi remoteAi;
    private final AiService aiService;
    private final Fisherman lobbyFisherman;
    private final Hub lobbyHub;
    private final TaskExecutor actionsExecutor;
    private final HookMemo hookMemo;

    private final int TIMEOUT = 200;

    @Scheduled(fixedRateString = "${cron.frequency.hook-tick}")
    void hookTick() {
        CompletableFuture.supplyAsync(lobbyHub::getHooks)
            .thenAccept(hookMemo::putAll)
            .orTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Scheduled(fixedRateString = "${cron.frequency.heart-beat}")
    void heartBeat() {
        CompletableFuture.supplyAsync(lobbyHub::getCount, actionsExecutor)
            .thenAccept(lobbyHub::nbPlayers)
            .orTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
    }

    @Scheduled(fixedRateString = "${cron.frequency.hook-cleanup-dead}")
    void hookCleanupDead() {
        lobbyFisherman.cleanup();
    }

    @Scheduled(fixedRateString = "${cron.frequency.hook-cleanup-old}")
    void hookCleanupOld() {
        hookRepo.cleanupOld();
    }

    @Scheduled(fixedRateString = "${cron.frequency.online-username}")
    void onlineUsername() {
        userRepo.updateOnlineUserNames(usernameMemo.keys());
    }

    @Scheduled(fixedRateString = "${cron.frequency.game-cleanup-unplayed}")
    void gameCleanupUnplayed() {
        System.out.println("[cron] remove old unplayed games");
        gameRepo.cleanupUnplayed();
    }

    @Scheduled(fixedRateString = "${cron.frequency.game-auto-finish}")
    void gameAutoFinish() {
        gameFinishCommand.apply();
    }

    @Scheduled(fixedRateString = "${cron.frequency.remote-ai-health}")
    void remoteAiHealth() {
        boolean health = remoteAi.health();
        aiService.setRemoteAiHealth(health);
        if (health) System.out.println("remote AI is up");
        else System.out.println("remote AI is down");
    }
}

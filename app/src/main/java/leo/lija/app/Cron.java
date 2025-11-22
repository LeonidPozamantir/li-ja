package leo.lija.app;

import jakarta.annotation.PostConstruct;
import leo.lija.app.ai.RemoteAi;
import leo.lija.app.command.GameFinishCommand;
import leo.lija.app.db.GameRepo;
import leo.lija.app.db.HookRepo;
import leo.lija.app.db.UserRepo;
import leo.lija.app.lobby.Fisherman;
import leo.lija.app.memo.HookMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnWebApplication
@ConditionalOnBooleanProperty(name = "ai.server", havingValue = false)
@RequiredArgsConstructor
public class Cron {

    private final TaskScheduler taskScheduler;
    private final TaskExecutor actionsExecutor;

    private final UserRepo userRepo;
    private final HookRepo hookRepo;
    private final GameRepo gameRepo;
    private final GameFinishCommand gameFinishCommand;
    private final RemoteAi remoteAi;
    private final Fisherman lobbyFisherman;
    private final leo.lija.app.site.Hub siteHub;
    private final leo.lija.app.lobby.Hub lobbyHub;
    private final HookMemo hookMemo;

    private final int TIMEOUT = 200;

    @PostConstruct
    void hookTick() {
        spawn(Duration.ofSeconds(1), () -> CompletableFuture.runAsync(() -> lobbyHub.withHooks(hookMemo::putAll), actionsExecutor)
            .orTimeout(TIMEOUT, TimeUnit.MILLISECONDS));
    }

    @PostConstruct
    void nbPlayers() {
        spawn(Duration.ofSeconds(1), () ->
            siteHub.nbPlayers().join()
        );
    }

    @PostConstruct
    void hookCleanupDead() {
        spawn(Duration.ofSeconds(2), lobbyFisherman::cleanup);
    }

    @PostConstruct
    void hookCleanupOld() {
        spawn(Duration.ofSeconds(21), hookRepo::cleanupOld);
    }

    @PostConstruct
    void onlineUsername() {
        spawn(Duration.ofSeconds(3), () ->
            siteHub.getUsernames()
                .thenAccept(userRepo::updateOnlineUserNames)
                .join()
        );
    }

    @PostConstruct
    void gameCleanupUnplayed() {
        spawn(Duration.ofHours(2), () -> {
            System.out.println("[cron] remove old unplayed games");
            gameRepo.cleanupUnplayed();
        });
    }

    @PostConstruct
    void gameAutoFinish() {
        spawn(Duration.ofHours(1), gameFinishCommand::apply);
    }

    @PostConstruct
    void remoteAiHealth() {
        spawn(Duration.ofSeconds(10), remoteAi::diagnose);
    }

    private void spawn(Duration freq, Runnable op) {
        taskScheduler.scheduleWithFixedDelay(op, RichDuration.randomize(freq));
    }

}

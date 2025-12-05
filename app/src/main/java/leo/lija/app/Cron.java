package leo.lija.app;

import jakarta.annotation.PostConstruct;
import leo.lija.app.ai.RemoteAi;
import leo.lija.app.command.GameCleanNext;
import leo.lija.app.command.GameFinish;
import leo.lija.app.db.GameRepo;
import leo.lija.app.db.HookRepo;
import leo.lija.app.db.UserRepo;
import leo.lija.app.game.HubMemo;
import leo.lija.app.lobby.Fisherman;
import leo.lija.app.memo.HookMemo;
import leo.lija.app.reporting.Reporting;
import leo.lija.app.socket.HubActor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
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
    private final GameFinish gameFinish;
    private final GameCleanNext gameCleanNext;
    private final RemoteAi remoteAi;
    private final Fisherman lobbyFisherman;
    private final leo.lija.app.site.Hub siteHub;
    private final leo.lija.app.lobby.Hub lobbyHub;
    private final HubMemo gameHubMemo;
    private final HookMemo hookMemo;
    private final Reporting reporting;

    private static final int TIMEOUT = 500;

    @PostConstruct
    void scheduleTasks() {
        spawn(Duration.ofSeconds(5), () -> {
            siteHub.broom();
            lobbyHub.broom();
            gameHubMemo.hubs().forEach(HubActor::broom);
        });

        spawn(Duration.ofSeconds(2), reporting::update);

        spawn(Duration.ofSeconds(1), () -> CompletableFuture.runAsync(() -> lobbyHub.withHooks(hookMemo::putAll), actionsExecutor)
            .orTimeout(TIMEOUT, TimeUnit.MILLISECONDS));

        spawn(Duration.ofSeconds(2), () ->
            siteHub.nbMembers().join()
        );

        spawn(Duration.ofSeconds(2), lobbyFisherman::cleanup);

        spawn(Duration.ofSeconds(21), hookRepo::cleanupOld);

        spawn(Duration.ofSeconds(3), () ->
            siteHub.withUsernames(userRepo::updateOnlineUserNames)
        );

        spawn(Duration.ofMinutes((long) (60 * 4.1)), () -> {
            gameRepo.cleanupUnplayed();
            gameCleanNext.apply();
        });

        spawn(Duration.ofHours(1), gameFinish::apply);

        spawn(Duration.ofSeconds(10), remoteAi::diagnose);
    }

    private void spawn(Duration freq, Runnable op) {
        taskScheduler.scheduleWithFixedDelay(op, Instant.now().plus(freq), RichDuration.randomize(freq));
    }

}

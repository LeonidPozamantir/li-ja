package leo.lija.app;

import leo.lija.app.command.GameFinishCommand;
import leo.lija.app.db.GameRepo;
import leo.lija.app.db.HookRepo;
import leo.lija.app.db.UserRepo;
import leo.lija.app.memo.HookMemo;
import leo.lija.app.memo.LobbyMemo;
import leo.lija.app.memo.UsernameMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnWebApplication
@RequiredArgsConstructor
public class Cron {

    private final UserRepo userRepo;
    private final UsernameMemo usernameMemo;
    private final HookRepo hookRepo;
    private final HookMemo hookMemo;
    private final LobbyMemo lobbyMemo;
    private final GameRepo gameRepo;
    private final GameFinishCommand gameFinishCommand;

    @Scheduled(fixedRateString = "${cron.online-username.frequency}")
    void onlineUsername() {
        userRepo.updateOnlineUserNames(usernameMemo.keys());
    }

//    @Scheduled(fixedRateString = "${cron.hook-cleanup-dead.frequency}")
//    void hookCleanupDead() {
//        boolean hasRemoved = hookRepo.keepOnlyOwnerIds(hookMemo.keys());
//        if (hasRemoved) lobbyMemo.increase();
//    }

    @Scheduled(fixedRateString = "${cron.hook-cleanup-old.frequency}")
    void hookCleanupOld() {
        hookRepo.cleanupOld();
    }

    @Scheduled(fixedRateString = "${cron.game-cleanup-unplayed.frequency}")
    void gameCleanupUnplayed() {
        System.out.println("[cron] remove old unplayed games");
        gameRepo.cleanupUnplayed();
    }

    @Scheduled(fixedRateString = "${cron.game-auto-finish.frequency}")
    void gameAutoFinish() {
        gameFinishCommand.apply();
    }
}

package leo.lija.app;

import leo.lija.system.Finisher;
import leo.lija.system.command.GameFinishCommand;
import leo.lija.system.db.GameRepo;
import leo.lija.system.db.HookRepo;
import leo.lija.system.db.UserRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.memo.HookMemo;
import leo.lija.system.memo.LobbyMemo;
import leo.lija.system.memo.UsernameMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
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

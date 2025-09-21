package leo.lija.app;

import leo.lija.system.db.HookRepo;
import leo.lija.system.db.UserRepo;
import leo.lija.system.memo.HookMemo;
import leo.lija.system.memo.LobbyMemo;
import leo.lija.system.memo.UsernameMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Cron {

    private final UserRepo userRepo;
    private final UsernameMemo usernameMemo;
    private final HookRepo hookRepo;
    private final HookMemo hookMemo;
    private final LobbyMemo lobbyMemo;

    @Scheduled(fixedRateString = "${cron.online-username.frequency}")
    void onlineUsername() {
        userRepo.updateOnlineUserNames(usernameMemo.keys());
    }

    @Scheduled(fixedRateString = "${cron.hook-cleanup-dead.frequency}")
    void hookCleanupDead() {
        boolean hasRemoved = hookRepo.keepOnlyIds(hookMemo.keys());
        if (hasRemoved) lobbyMemo.increase();
    }

    @Scheduled(fixedRateString = "${cron.hook-cleanup-old.frequency}")
    void hookCleanupOld() {
        hookRepo.cleanupOld();
    }
}

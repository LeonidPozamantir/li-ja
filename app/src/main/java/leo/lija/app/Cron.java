package leo.lija.app;

import leo.lija.system.db.UserRepo;
import leo.lija.system.memo.UsernameMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Cron {

    private final UserRepo userRepo;
    private final UsernameMemo usernameMemo;

    @Scheduled(fixedRateString = "${cron.online-username.frequency}")
    void onlineUsername() {
        userRepo.updateOnlineUserNames(usernameMemo.keys());
    }
}

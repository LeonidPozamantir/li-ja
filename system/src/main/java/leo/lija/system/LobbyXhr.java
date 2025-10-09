package leo.lija.system;

import leo.lija.system.db.GameRepo;
import leo.lija.system.db.HookRepo;
import leo.lija.system.memo.LobbyMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LobbyXhr {

    private final HookRepo hookRepo;
    private final GameRepo gameRepo;
    private final LobbyMemo lobbyMemo;

    public void cancel(String ownerId) {
        hookRepo.deleteByOwnerId(ownerId);
        versionInc();
    }

    private int versionInc() {
        return lobbyMemo.increase();
    }
}

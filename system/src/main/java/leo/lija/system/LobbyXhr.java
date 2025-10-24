package leo.lija.system;

import leo.lija.system.db.HookRepo;
import leo.lija.system.memo.HookMemo;
import leo.lija.system.memo.LobbyMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LobbyXhr {

    private final HookRepo hookRepo;
    private final LobbyMemo lobbyMemo;
    private final HookMemo hookMemo;

    public void cancel(String ownerId) {
        hookRepo.deleteByOwnerId(ownerId);
        hookMemo.remove(ownerId);
        versionInc();
    }

    private int versionInc() {
        return lobbyMemo.increase();
    }
}

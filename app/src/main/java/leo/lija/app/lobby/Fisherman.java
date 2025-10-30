package leo.lija.app.lobby;

import leo.lija.app.db.HookRepo;
import leo.lija.app.entities.Hook;
import leo.lija.app.memo.HookMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Fisherman {

    private final HookRepo hookRepo;
    private final HookMemo hookMemo;
    private final Lobby socket;

    // DO delete in db
    public void remove(Hook hook) {
        hookRepo.deleteById(hook.getId());
        hookMemo.remove(hook.getOwnerId());
        socket.removeHook(hook);
    }

    // DO NOT insert in db (done on client side)
    public void add(Hook hook) {
        hookMemo.put(hook.getOwnerId());
        socket.addHook(hook);
    }

    public void cleanup() {
        hookRepo.unmatchedNotInOwnerIds(hookMemo.keys())
                .forEach(this::remove);
    }
}

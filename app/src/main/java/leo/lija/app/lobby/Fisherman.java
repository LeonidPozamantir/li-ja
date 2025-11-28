package leo.lija.app.lobby;

import leo.lija.app.db.HookRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Hook;
import leo.lija.app.memo.HookMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Fisherman {

    private final HookRepo hookRepo;
    private final HookMemo hookMemo;
    private final Socket socket;

    // DO delete in db
    public void delete(Hook hook) {
        socket.removeHook(hook);
        hookRepo.deleteById(hook.getId());
    }

    // DO NOT insert in db (done on client side)
    public void add(Hook hook) {
        socket.addHook(hook);
        shake(hook);
    }

    public void bite(Hook hook, DbGame game) {
        socket.removeHook(hook);
        socket.biteHook(hook, game);
    }

    public void shake(Hook hook) {
        hookMemo.put(hook.getOwnerId());
    }

    public void cleanup() {
        hookRepo.unmatchedNotInOwnerIds(hookMemo.keys())
                .forEach(this::delete);
    }
}

package leo.lija.app.lobby;

import leo.lija.app.memo.HookMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class HookPool {

    private final HookMemo hookMemo;

    private final Set<String> ownerIds = ConcurrentHashMap.newKeySet();

    public void register(String id) {
        ownerIds.add(id);
    }

    public void unregister(String id) {
        ownerIds.remove(id);
    }

    public void tick() {
        ownerIds.forEach(hookMemo::put);
    }
}

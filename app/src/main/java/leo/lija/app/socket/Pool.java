package leo.lija.app.socket;

import leo.lija.app.memo.SocketMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class Pool {

    private final SocketMemo socketMemo;

    private final Set<String> uids = ConcurrentHashMap.newKeySet();

    public void register(String uid) {
        uids.add(uid);
        socketMemo.put(uid);
    }

    public void unregister(String uid) {
        uids.remove(uid);
        socketMemo.remove(uid);
    }

    public void tick() {
        uids.forEach(socketMemo::put);
    }
}

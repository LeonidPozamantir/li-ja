package leo.lija.system.memo;

import leo.lija.system.config.MemoConfig;
import org.springframework.stereotype.Service;

@Service
public class WatcherMemo extends BooleanExpiryMemo {

    public WatcherMemo(MemoConfig config) {
        super(config);
        cache = Builder.expiry(config.watcher().timeout());
    }

    public long count(String prefix) {
        return keys().stream().filter(k -> k.startsWith(prefix)).count();
    }
}

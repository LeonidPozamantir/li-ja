package leo.lija.system.memo;

import leo.lija.system.config.MemoConfigProperties;
import org.springframework.stereotype.Service;

@Service
public class WatcherMemo extends BooleanExpiryMemo {

    public WatcherMemo(MemoConfigProperties config) {
        super(config);
        cache = Builder.expiry(config.watcher().timeout());
    }

    public long count(String prefix) {
        return keys().stream().filter(k -> k.startsWith(prefix)).count();
    }
}

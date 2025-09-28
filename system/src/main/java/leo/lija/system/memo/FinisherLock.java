package leo.lija.system.memo;

import leo.lija.system.config.MemoConfigProperties;
import leo.lija.system.entities.DbGame;
import org.springframework.stereotype.Service;

@Service
public class FinisherLock extends BooleanExpiryMemo {

    public FinisherLock(MemoConfigProperties config) {
        super(config);
        cache = Builder.expiry(config.finisherLock().timeout());
    }

    public boolean isLocked(DbGame game) {
        return get(game.getId());
    }

    public void lock(DbGame game) {
        put(game.getId());
    }
}

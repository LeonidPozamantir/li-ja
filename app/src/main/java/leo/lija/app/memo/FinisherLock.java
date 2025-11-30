package leo.lija.app.memo;

import leo.lija.app.config.MemoConfigProperties;
import leo.lija.app.entities.DbGame;
import org.springframework.stereotype.Service;

@Service
public class FinisherLock extends BooleanExpiryMemo {

    public FinisherLock(MemoConfigProperties config) {
        super(config.finisherLock().timeout());
    }

    public boolean isLocked(DbGame game) {
        return get(game.getId());
    }

    public void lock(DbGame game) {
        put(game.getId());
    }
}

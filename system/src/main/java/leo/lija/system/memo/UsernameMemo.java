package leo.lija.system.memo;

import leo.lija.system.config.MemoConfig;
import org.springframework.stereotype.Service;

@Service
public class UsernameMemo extends BooleanExpiryMemo {

    public UsernameMemo(MemoConfig config) {
        super(config);
        cache = Builder.expiry(config.username().timeout());
    }
}

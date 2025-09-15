package leo.lija.system.memo;

import leo.lija.system.config.MemoConfigProperties;
import org.springframework.stereotype.Service;

@Service
public class UsernameMemo extends BooleanExpiryMemo {

    public UsernameMemo(MemoConfigProperties config) {
        super(config);
        cache = Builder.expiry(config.username().timeout());
    }
}

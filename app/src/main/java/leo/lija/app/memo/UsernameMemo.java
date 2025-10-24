package leo.lija.app.memo;

import leo.lija.app.config.MemoConfigProperties;
import org.springframework.stereotype.Service;

@Service
public class UsernameMemo extends BooleanExpiryMemo {

    public UsernameMemo(MemoConfigProperties config) {
        super(config);
        cache = Builder.expiry(config.username().timeout());
    }
}

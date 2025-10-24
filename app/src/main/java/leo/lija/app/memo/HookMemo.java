package leo.lija.app.memo;

import leo.lija.app.config.MemoConfigProperties;
import org.springframework.stereotype.Service;

@Service
public class HookMemo extends BooleanExpiryMemo {

    public HookMemo(MemoConfigProperties config) {
        super(config);
        cache = Builder.expiry(config.hook().timeout());
    }
}

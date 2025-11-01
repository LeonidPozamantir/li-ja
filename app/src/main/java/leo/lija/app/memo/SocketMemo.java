package leo.lija.app.memo;

import leo.lija.app.config.MemoConfigProperties;
import org.springframework.stereotype.Service;

@Service
public class SocketMemo extends BooleanExpiryMemo {

    public SocketMemo(MemoConfigProperties config) {
        super(config);
        cache = Builder.expiry(config.socket().timeout());
    }
}

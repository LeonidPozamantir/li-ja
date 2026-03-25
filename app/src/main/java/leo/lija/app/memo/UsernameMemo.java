package leo.lija.app.memo;

import leo.lija.app.config.MemoConfigProperties;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UsernameMemo extends BooleanExpiryMemo {

    public UsernameMemo(MemoConfigProperties config) {
        super(config.username().timeout());
    }

    public String normalize(String name) {
        return name.toLowerCase();
    }

    @Override
    public boolean get(String key) {
        return super.get(normalize(key));
    }

    @Override
    public void put(String key) {
        super.put(normalize(key));
    }

    @Override
    public void putAll(Collection<String> keys) {
        super.putAll(keys.stream().map(this::normalize).toList());
    }

}

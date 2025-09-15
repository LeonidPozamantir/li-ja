package leo.lija.system.memo;

import com.google.common.cache.Cache;
import leo.lija.system.config.MemoConfigProperties;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public abstract class BooleanExpiryMemo {

    private final MemoConfigProperties config;

    protected Cache<String, Boolean> cache;

    public boolean get(String key) {
        return Optional.ofNullable(cache.getIfPresent(key)).orElse(false);
    }

    public void put(String key) {
        cache.put(key, true);
    }

    public Set<String> keys() {
        return cache.asMap().keySet();
    }

    public long count() {
        return cache.size();
    }
}

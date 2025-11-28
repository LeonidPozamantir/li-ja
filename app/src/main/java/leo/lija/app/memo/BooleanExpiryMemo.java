package leo.lija.app.memo;

import com.google.common.cache.Cache;
import leo.lija.app.config.MemoConfigProperties;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
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

    public void putAll(Collection<String> keys) {
        keys.forEach(k -> cache.put(k, true));
    }

    public void remove(String key) {
        cache.invalidate(key);
    }

    public Set<String> keys() {
        return cache.asMap().keySet();
    }

    public long count() {
        return cache.size();
    }
}

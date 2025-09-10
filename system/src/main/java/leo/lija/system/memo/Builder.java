package leo.lija.system.memo;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Builder {

    public static <K, V> LoadingCache<K, V> cache(int ttl, Function<K, V> f) {
        return CacheBuilder.newBuilder()
            .expireAfterWrite(ttl, TimeUnit.SECONDS)
            .build(new CacheLoader<K, V>() {
                public V load(K key) {
                    return f.apply(key);
                }
            });
    }
}

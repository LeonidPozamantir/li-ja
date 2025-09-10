package leo.lija.system;

import com.google.common.cache.LoadingCache;
import leo.lija.system.memo.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("memo cache should")
class MemoBuilderTest {

    Function<String, Integer> f = String::length;
    LoadingCache<String, Integer> cache = Builder.cache(10, f);

    @Test
    @DisplayName("f")
    void testF() {
        assertThat(f.apply("test")).isEqualTo(4);
    }

    @Test
    @DisplayName("compute missing value")
    void missingValue() {
        assertThat(cache.getUnchecked("test")).isEqualTo(4);
    }

    @Test
    @DisplayName("return stored value")
    void storedValue() {
        LoadingCache<Object, Integer> c = Builder.cache(10, s -> new Random().nextInt());
        int a = c.getUnchecked("test");
        assertThat(c.getUnchecked("test")).isEqualTo(a);
    }
}

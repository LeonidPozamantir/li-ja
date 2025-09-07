package leo.lija.system;

import leo.lija.system.memo.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("memo cache should")
class MemoBuilderTest {

    Function<String, Integer> f = String::length;
    Function<String, Integer> cache = Builder.cache(10, f);

    @Test
    @DisplayName("f")
    void testF() {
        assertThat(f.apply("test")).isEqualTo(4);
    }

    @Test
    @DisplayName("compute missing value")
    void missingValue() {
        assertThat(cache.apply("test")).isEqualTo(4);
    }

    @Test
    @DisplayName("return stored value")
    void storedValue() {
        Function<Object, Integer> c = Builder.cache(10, s -> new Random().nextInt());
        int a = c.apply("test");
        assertThat(c.apply("test")).isEqualTo(a);
    }
}

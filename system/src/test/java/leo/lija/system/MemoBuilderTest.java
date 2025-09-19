package leo.lija.system;

import com.google.common.cache.Cache;
import com.google.common.cache.LoadingCache;
import leo.lija.system.memo.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Random;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;


class MemoBuilderTest {

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("memo cache should")
    class MemoCache {

        Function<String, Integer> f = String::length;
        LoadingCache<String, Integer> makeCache = Builder.cache(10, f);

        @Test
        @Order(1)
        @DisplayName("f")
        void testF() {
            assertThat(f.apply("test")).isEqualTo(4);
        }

        @Test
        @Order(2)
        @DisplayName("compute missing value")
        void missingValue() {
            assertThat(makeCache.getUnchecked("test")).isEqualTo(4);
        }

        @Test
        @Order(3)
        @DisplayName("return stored value")
        void storedValue() {
            LoadingCache<Object, Integer> c = Builder.cache(10, s -> new Random().nextInt());
            int a = c.getUnchecked("test");
            assertThat(c.getUnchecked("test")).isEqualTo(a);
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("expiry cache should")
    class ExpiryCache {
        Cache<String, Integer> makeCache = Builder.expiry(10);

        @Test
        @Order(1)
        @DisplayName("be empty")
        void empty() {
            assertThat(makeCache.size()).isZero();
        }

        @Test
        @Order(2)
        @DisplayName("read missing value")
        void missingValue() {
            assertThat(makeCache.getIfPresent("three")).isNull();
        }

        @Test
        @Order(3)
        @DisplayName("store value")
        void store() {
            makeCache.put("three", 3);
            assertThat(makeCache.size()).isEqualTo(1);
        }

        @Test
        @Order(4)
        @DisplayName("read stored value")
        void readStored() {
            makeCache.put("three", 3);
            assertThat(makeCache.getIfPresent("three")).isEqualTo(3);
        }
    }
}

package leo.lija.system;

import leo.lija.system.memo.AliveMemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static leo.lija.chess.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AliveMemoTest extends Fixtures {

    @Autowired
    private AliveMemo memo;

    @Test
    @Order(1)
    @DisplayName("non existing key")
    void nonExistingKey() {
        assertThat(memo.get("arst", WHITE)).isEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("put key")
    void putKey() {
        assertDoesNotThrow(() -> memo.put("arst", WHITE));
    }

    @Nested
    @Order(3)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("read key")
    class ReadKey {

        @Test
        @Order(1)
        @DisplayName("success put")
        void successPut() {
            assertDoesNotThrow(() -> memo.put("arst", WHITE));
        }

        @Test
        @Order(2)
        void count() {
            assertThat(memo.count()).isEqualTo(1);
        }

        @Test
        @Order(3)
        void get() {
            assertThat(memo.get("arst", WHITE)).isPresent();
        }
    }
}

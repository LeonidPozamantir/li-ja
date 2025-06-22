package leo.lija.chess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Color should")
class ColorTest {

    @Test
    @DisplayName("have correct opposite color")
    void oppositeColor() {
        assertThat(WHITE.getOpposite()).isEqualTo(BLACK);
        assertThat(BLACK.getOpposite()).isEqualTo(WHITE);
    }
}

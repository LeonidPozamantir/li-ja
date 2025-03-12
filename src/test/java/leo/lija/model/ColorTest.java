package leo.lija.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Color should")
public class ColorTest {

    @Test
    @DisplayName("have correct opposite color")
    void oppositeColor() {
        assertThat(WHITE.getOpposite()).isEqualTo(BLACK);
        assertThat(BLACK.getOpposite()).isEqualTo(WHITE);
    }
}

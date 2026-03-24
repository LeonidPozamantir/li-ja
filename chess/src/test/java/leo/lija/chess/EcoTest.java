package leo.lija.chess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EcoTest {

    @Test
    @DisplayName("complete game")
    void completeGame() {
        String game = "d4 Nf6 e4 Nxe4 f3 Nd6 g3";
        assertThat(Eco.openingOf(game)).map(Opening::name).contains("Queen's Pawn Game");
    }
}

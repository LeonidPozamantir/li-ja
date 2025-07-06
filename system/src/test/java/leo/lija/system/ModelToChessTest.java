package leo.lija.system;

import leo.lija.chess.Game;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("model to chess conversion")
class ModelToChessTest extends Fixtures {

    @Test
    @DisplayName("new game")
    void testNewGame() {
        assertThat(newDbGame.toChess()).isEqualTo(Game.newGame());
    }
}

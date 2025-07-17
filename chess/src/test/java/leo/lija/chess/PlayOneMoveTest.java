package leo.lija.chess;

import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E4;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("playing a move should")
class PlayOneMoveTest {

    @Test
    @DisplayName("only process things once")
    void processOnce() {
        assertDoesNotThrow(() -> new RichGame().playMoves(Pair.of(E2, E4)));
    }
}

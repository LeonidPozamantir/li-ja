package leo.lija.chess;

import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E4;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("play with a clock")
class ClockTest {

    Clock clock = new PausedClock(5 * 60 * 1000, 0);
    RichGame game = new RichGame().withClock(clock);

    @Test
    @DisplayName("new game")
    void newGame() {
        assertThat(game.clock).matches(oc -> oc.isPresent() && oc.get().getColor() == WHITE);
    }

    @Test
    @DisplayName("one move played")
    void oneMove() {
        assertThat(game.playMoves(Pair.of(E2, E4)).clock).matches(oc -> oc.isPresent() && oc.get().getColor() == BLACK);
    }

}

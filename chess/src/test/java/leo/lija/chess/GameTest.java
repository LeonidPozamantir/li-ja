package leo.lija.chess;

import io.vavr.Tuple2;
import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Pos.D5;
import static leo.lija.chess.Pos.D7;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E4;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("capturing a piece should")
class GameTest extends BaseChess {

    @Test
    @DisplayName("add it to the dead pieces")
    void addToDead() {
        RichGame game = new RichGame().playMoves(Pair.of(E2, E4), Pair.of(D7, D5), Pair.of(E4, D5));
        assertThat(game.deads).containsExactlyInAnyOrder(new Tuple2<>(D5, BLACK.pawn()));
    }

}

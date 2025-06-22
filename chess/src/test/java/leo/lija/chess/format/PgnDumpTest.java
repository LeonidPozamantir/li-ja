package leo.lija.chess.format;

import leo.lija.chess.Game;
import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A4;
import static leo.lija.chess.Pos.B4;
import static leo.lija.chess.Pos.B5;
import static leo.lija.chess.Pos.B7;
import static leo.lija.chess.Pos.C2;
import static leo.lija.chess.Pos.C4;
import static leo.lija.chess.Pos.C6;
import static leo.lija.chess.Pos.C7;
import static leo.lija.chess.Pos.D1;
import static leo.lija.chess.Pos.D2;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.D5;
import static leo.lija.chess.Pos.D7;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E3;
import static leo.lija.chess.Pos.F3;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("complete game dump should")
class PgnDumpTest {

    @Nested
    @DisplayName("only moves")
    class onlyMoves {

        @Test
        @DisplayName("Gioachino Greco")
        void greco() {
            Game game = Game.newGame().playMoves(Pair.of(D2, D4), Pair.of(D7, D5), Pair.of(C2, C4), Pair.of(D5, C4), Pair.of(E2, E3), Pair.of(B7, B5),
                Pair.of(A2, A4), Pair.of(C7, C6), Pair.of(A4, B5), Pair.of(C6, B5), Pair.of(D1, F3));
            assertThat(game.pgnMoves()).containsExactlyInAnyOrder("d4 d5 c4 dxc4 e3 b5 a4 c6 axb5 cxb5 Qf3".split(" "));
        }

    }

}

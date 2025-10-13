package leo.lija.chess;

import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.A1;
import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A3;
import static leo.lija.chess.Pos.B2;
import static leo.lija.chess.Pos.D5;
import static leo.lija.chess.Pos.D7;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Side.QUEEN_SIDE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("capturing a piece should")
class GameTest extends BaseChess {

    @Test
    @DisplayName("add it to the dead pieces")
    void addToDead() {
        RichGame game = new RichGame().playMoves(Pair.of(E2, E4), Pair.of(D7, D5), Pair.of(E4, D5));
        assertThat(game.deads).containsExactlyInAnyOrder(Pair.of(D5, BLACK.pawn()));
    }

    @Test
    @DisplayName("recapture a piece should add both to the dead pieces")
    void addBoth() {
        RichGame game = new RichGame(visual.str2Obj("""
bq
R"""));
        assertThat(game.playMoves(Pair.of(A1, A2), Pair.of(B2, A2)).deads).containsExactlyInAnyOrder(Pair.of(A2, BLACK.bishop()), Pair.of(A2, WHITE.rook()));

    }

    @Nested
    @DisplayName("prevent castle by capturing a rook")
    class PreventCastleCapturing {
        RichGame game = new RichGame(visual.str2Obj("""
 b
R   K"""), BLACK);

        @Test
        @DisplayName("can castle queenside")
        void queenside() {
            assertThat(game.board.getHistory().canCastle(WHITE, QUEEN_SIDE)).isTrue();
        }

        @Test
        @DisplayName("can still castle queenside")
        void stillCanQueenside() {
            assertThat(game.playMoves(Pair.of(B2, A3)).board.getHistory().canCastle(WHITE, QUEEN_SIDE)).isTrue();
        }

        @Test
        @DisplayName("can not castle queenside anymore")
        void cantCastle() {
            assertThat(game.playMoves(Pair.of(B2, A1)).board.getHistory().canCastle(WHITE, QUEEN_SIDE)).isFalse();
        }
    }
}

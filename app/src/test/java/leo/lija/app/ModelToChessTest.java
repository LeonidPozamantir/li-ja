package leo.lija.app;

import leo.lija.chess.Game;
import leo.lija.chess.PausedClock;
import leo.lija.chess.format.VisualFormat;
import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.A3;
import static leo.lija.chess.Pos.A7;
import static leo.lija.chess.Pos.A8;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.C7;
import static leo.lija.chess.Pos.F5;
import static leo.lija.chess.Pos.H1;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("model to chess conversion")
class ModelToChessTest extends Fixtures {

    private VisualFormat visual = new VisualFormat();

    @Test
    @DisplayName("new game")
    void testNewGame() {
        assertThat(newDbGame.toChess()).isEqualTo(new Game());
    }

    @Nested
    @DisplayName("played game")
    class PlayedGame {
        Game game = dbGame1.toChess();

        @Test
        void player() {
            assertThat(game.getPlayer()).isEqualTo(WHITE);
        }

        @Test
        @DisplayName("pgn moves")
        void pgnMoves() {
            assertThat(game.getPgnMoves()).isEqualTo("e4 Nc6 Nf3 Nf6 e5 Ne4 d3 Nc5 Be3 d6 d4 Ne4 Bd3 Bf5 Nc3 Nxc3 bxc3 Qd7 Bxf5 Qxf5 Nh4 Qe4 g3 Qxh1+");
        }

        @Test
        void pieces() {
            assertThat(visual.newLine(game.getBoard().toString())).isEqualTo("""
r   kb r
ppp pppp
  np
    P
   P   N
  P B P
P P  P P
R  QK  q
""");
        }

        @Test
        void deads() {
            assertThat(game.getDeads()).containsExactlyInAnyOrder(
                Pair.of(C3, BLACK.knight()),
                Pair.of(F5, BLACK.bishop()),
                Pair.of(F5, WHITE.bishop()),
                Pair.of(C3, WHITE.knight()),
                Pair.of(H1, WHITE.rook()));
        }

        @Test
        @DisplayName("last move")
        void lastMove() {
            assertThat(game.getBoard().getHistory().lastMove()).isEmpty();
        }

        @Test
        void clock() {
            assertThat(game.getClock()).isEmpty();
        }
    }

    @Nested
    @DisplayName("and another played game")
    class AnotherPlayedGame {
        Game game = dbGame2.toChess();

        @Test
        void player() {
            assertThat(game.getPlayer()).isEqualTo(BLACK);
        }

        @Test
        @DisplayName("pgn moves")
        void pgnMoves() {
            assertThat(game.getPgnMoves()).isEqualTo("e4 e5 Qh5 Qf6 Nf3 g6 Qxe5+ Qxe5 Nxe5 Nf6 Nc3 Nc6 Nxc6 bxc6 e5 Nd5 Nxd5 cxd5 d4 Rb8 c3 d6 Be2 dxe5 dxe5 Rg8 Bf3 d4 cxd4 Bb4+ Ke2 g5 a3 g4 Bc6+ Bd7 Bxd7+ Kxd7 axb4 Rxb4 Kd3 Rb3+ Kc4 Rb6 Rxa7 Rc6+ Kb5 Rb8+ Ka5 Rc4 Rd1 Kc6 d5+ Kc5 Rxc7#");
        }

        @Test
        void pieces() {
            assertThat(visual.newLine(game.getBoard().toString())).isEqualTo("""
 r
  R  p p

K kPP
  r   p

 P   PPP
  BR
""");
        }

        @Test
        @DisplayName("last move")
        void lastMove() {
            assertThat(game.getBoard().getHistory().lastMove()).contains(Pair.of(A7, C7));
        }

        @Test
        void clock() {
            assertThat(game.getClock()).contains(new PausedClock(
                1200,
                5,
                BLACK,
                196250,
                304100
            ));
        }
    }

    @Nested
    @DisplayName("a chess960 played game")
    class Game960 {
        Game game = dbGame3.toChess();

        @Test
        void player() {
            assertThat(game.getPlayer()).isEqualTo(BLACK);
        }

        @Test
        void pieces() {
            assertThat(visual.newLine(game.getBoard().toString())).isEqualTo("""
R  k
       R


 B   pp
 P P P
  K

""");
        }

        @Test
        @DisplayName("last move")
        void lastMove() {
            assertThat(game.getBoard().getHistory().lastMove()).contains(Pair.of(A3, A8));
        }
    }
}

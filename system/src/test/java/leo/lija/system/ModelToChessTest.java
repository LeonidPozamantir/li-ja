package leo.lija.system;

import leo.lija.chess.Game;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static leo.lija.chess.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("model to chess conversion")
class ModelToChessTest extends Fixtures {

//    @Test
//    @DisplayName("new game")
//    void testNewGame() {
//        assertThat(newDbGame.toChess()).isEqualTo(Game.newGame());
//    }

    @Nested
    @DisplayName("played game")
    class PlayedGame {
        Game game = new DbGame(
            "huhuhaha",
            List.of(
                newPlayer("white", "ip ar sp16 sN14 kp ub8 Bp6 dq Kp0 ek np LB12 wp22 Fn2 pp hR"),
                newPlayer("black", "Wp 4r Xp Qn1 Yp LB13 Rp9 hq17 0p 8k 1p 9b 2p sN3 3p ?r")
            ),
            "e4 Nc6 Nf3 Nf6 e5 Ne4 d3 Nc5 Be3 d6 d4 Ne4 Bd3 Bf5 Nc3 Nxc3 bxc3 Qd7 Bxf5 Qxf5 Nh4 Qe4 g3 Qxh1+",
            31,
            24,
            1
        ).toChess();

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
            assertThat(game.getBoard().toString()).isEqualTo("""
r   kb r
ppp pppp
  np
    P
   P   N
  P B P
P P  P P
R  QR  q
""");
        }
    }
}

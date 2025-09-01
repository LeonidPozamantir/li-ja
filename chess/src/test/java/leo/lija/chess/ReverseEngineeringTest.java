package leo.lija.chess;

import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Pos.A5;
import static leo.lija.chess.Pos.A7;
import static leo.lija.chess.Pos.B1;
import static leo.lija.chess.Pos.C1;
import static leo.lija.chess.Pos.C2;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.C4;
import static leo.lija.chess.Pos.C6;
import static leo.lija.chess.Pos.C7;
import static leo.lija.chess.Pos.D2;
import static leo.lija.chess.Pos.D3;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.E1;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.E5;
import static leo.lija.chess.Pos.E7;
import static leo.lija.chess.Pos.F1;
import static leo.lija.chess.Pos.F2;
import static leo.lija.chess.Pos.F6;
import static leo.lija.chess.Pos.G1;
import static leo.lija.chess.Pos.G5;
import static leo.lija.chess.Pos.G8;
import static leo.lija.chess.Pos.H3;
import static leo.lija.chess.Pos.H6;
import static leo.lija.chess.Pos.H7;
import static leo.lija.chess.Role.KNIGHT;
import static leo.lija.chess.Role.QUEEN;
import static org.assertj.core.api.Assertions.assertThat;

class ReverseEngineeringTest extends BaseChess {

    private Optional<Pair<Pos, Pos>> findMove(Game g1, Game g2) {
        return (new ReverseEngineering(g1, g2.board)).move();
    }

    private Game play(Game game, Pair<Pos, Pos> ...moves) {
        return new RichGame(game).playMoveList(Arrays.stream(moves).toList());
    }

    Game playedGame = play(new Game(), Pair.of(E2, E4), Pair.of(E7, E5), Pair.of(F1, C4), Pair.of(G8, F6), Pair.of(D2, D3), Pair.of(C7, C6),
        Pair.of(C1, G5), Pair.of(H7, H6), Pair.of(G1, H3), Pair.of(A7, A5));

    /*
rnbqkb r
 p p pp
  p  n p
p   p B
  B P
   P   N
PPP  PPP
RN QK  R
*/

    @Nested
    @DisplayName("reverse engineer a move")
    class ReverseEngineer {

        @Nested
        @DisplayName("none on same games")
        class SameGames {

            @Test
            @DisplayName("initial game")
            void initial() {
                assertThat(findMove(new Game(), new Game())).isEmpty();
            }

            @Test
            @DisplayName("played game")
            void played() {
                assertThat(findMove(playedGame, playedGame)).isEmpty();
            }
        }

        @Nested
        @DisplayName("none on different games")
        class DifferentGames {

            @Test
            @DisplayName("initial to played")
            void initial2Played() {
                assertThat(findMove(new Game(), playedGame)).isEmpty();
            }

            @Test
            @DisplayName("played to initial")
            void played2Initial() {
                assertThat(findMove(playedGame, new Game())).isEmpty();
            }
        }

        @Nested
        @DisplayName("find one move")
        class OneMove {

            @Test
            @DisplayName("initial game pawn moves one square")
            void initialPawnOneSquare() {
                assertThat(findMove(new Game(), play(new Game(), Pair.of(D2, D3)))).contains(Pair.of(D2, D3));
            }

            @Test
            @DisplayName("initial game pawn moves two squares")
            void initialPawnTwoSquares() {
                assertThat(findMove(new Game(), play(new Game(), Pair.of(D2, D4)))).contains(Pair.of(D2, D4));
            }

            @Test
            @DisplayName("initial game bishop moves") // actually knight :)
            void initialBishop() {
                assertThat(findMove(new Game(), play(new Game(), Pair.of(B1, C3)))).contains(Pair.of(B1, C3));
            }

            @Test
            @DisplayName("played game king moves right")
            void playedKingRight() {
                assertThat(findMove(playedGame, play(playedGame, Pair.of(E1, F1)))).contains(Pair.of(E1, F1));
            }

            @Test
            @DisplayName("played game bishop eats knight")
            void playedBishopKnight() {
                assertThat(findMove(playedGame, play(playedGame, Pair.of(G5, F6)))).contains(Pair.of(G5, F6));
            }

            @Test
            @DisplayName("played game king castles kingside")
            void playedKingCastlesKingside() {
                assertThat(findMove(playedGame, play(playedGame, Pair.of(E1, G1)))).contains(Pair.of(E1, G1));
            }

            @Nested
            class Promotion {
                RichGame game = new RichGame(visual.str2Obj("""
  p  k
K      """), BLACK);

                @Test
                @DisplayName("to queen")
                void queen() {
                    Game newGame = game.playMove(C2, C1, QUEEN);
                    assertThat(findMove(game, newGame)).contains(Pair.of(C2, C1));
                }

                @Test
                @DisplayName("to knight")
                void knight() {
                    Game newGame = game.playMove(C2, C1, KNIGHT);
                    assertThat(findMove(game, newGame)).contains(Pair.of(C2, C1));
                }

                @Test
                void not() {
                    Game newGame = game.playMove(F2, E2);
                    assertThat(findMove(game, newGame)).contains(Pair.of(F2, E2));
                }
            }
        }
    }
}

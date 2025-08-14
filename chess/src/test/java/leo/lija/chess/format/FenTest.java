package leo.lija.chess.format;

import leo.lija.chess.Game;
import leo.lija.chess.Pos;
import leo.lija.chess.RichGame;
import leo.lija.chess.Situation;
import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A3;
import static leo.lija.chess.Pos.A7;
import static leo.lija.chess.Pos.A8;
import static leo.lija.chess.Pos.B8;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.C4;
import static leo.lija.chess.Pos.C5;
import static leo.lija.chess.Pos.C7;
import static leo.lija.chess.Pos.C8;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.F1;
import static leo.lija.chess.Pos.F3;
import static leo.lija.chess.Pos.F4;
import static leo.lija.chess.Pos.G1;
import static leo.lija.chess.Pos.G8;
import static leo.lija.chess.Pos.H1;
import static leo.lija.chess.Pos.H6;
import static leo.lija.chess.Pos.H8;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("fen notation should")
class FenTest {

    Fen f = new Fen();

    @Nested
    class export {

        @Nested
        @DisplayName("game opening")
        class Opening {

            List<Pair<Pos, Pos>> moves = List.of(Pair.of(E2, E4), Pair.of(C7, C5), Pair.of(G1, F3), Pair.of(G8, H6), Pair.of(A2, A3));

            @Test
            @DisplayName("new game")
            void newGame() {
                assertThat(f.obj2Str(new Game())).isEqualTo("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            }

            @Test
            @DisplayName("one move")
            void oneMove() {
                assertThat(f.obj2Str(new RichGame().playMoveList(moves.stream().limit(1).toList())))
                    .isEqualTo("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
            }

            @Test
            @DisplayName("2 moves")
            void twoMoves() {
                assertThat(f.obj2Str(new RichGame().playMoveList(moves.stream().limit(2).toList())))
                    .isEqualTo("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2");
            }

            @Test
            @DisplayName("3 moves")
            void threeMoves() {
                assertThat(f.obj2Str(new RichGame().playMoveList(moves.stream().limit(3).toList())))
                    .isEqualTo("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
            }

            @Test
            @DisplayName("4 moves")
            void fourMoves() {
                assertThat(f.obj2Str(new RichGame().playMoveList(moves.stream().limit(4).toList())))
                    .isEqualTo("rnbqkb1r/pp1ppppp/7n/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3");
            }

            @Test
            @DisplayName("5 moves")
            void fiveMoves() {
                assertThat(f.obj2Str(new RichGame().playMoveList(moves.stream().limit(5).toList())))
                    .isEqualTo("rnbqkb1r/pp1ppppp/7n/2p5/4P3/P4N2/1PPP1PPP/RNBQKB1R b KQkq - 0 3");
            }
        }
    }

    @Nested
    class Import {

        @Nested
        class Torus {
            @Test
            @DisplayName("A8 + 1")
            void a8p1() {
                assertThat(f.tore(A8, 1)).contains(B8);
            }

            @Test
            @DisplayName("A8 + 2")
            void a8p2() {
                assertThat(f.tore(A8, 2)).contains(C8);
            }

            @Test
            @DisplayName("A8 + 7")
            void a8p7() {
                assertThat(f.tore(A8, 7)).contains(H8);
            }

            @Test
            @DisplayName("A8 + 8")
            void a8p8() {
                assertThat(f.tore(A8, 8)).contains(A7);
            }

            @Test
            @DisplayName("C4 + 3")
            void c4p3() {
                assertThat(f.tore(C4, 3)).contains(F4);
            }

            @Test
            @DisplayName("C4 + 8")
            void c4p8() {
                assertThat(f.tore(C4, 8)).contains(C3);
            }

            @Test
            @DisplayName("F1 + 2")
            void f1p2() {
                assertThat(f.tore(F1, 2)).contains(H1);
            }
        }

        List<Pair<Pos, Pos>> moves = List.of(Pair.of(E2, E4), Pair.of(C7, C5), Pair.of(G1, F3), Pair.of(G8, H6), Pair.of(A2, A3));

        private void compare(List<Pair<Pos, Pos>> ms, String fen) {
            Game g = new RichGame().playMoveList(ms);
            Optional<Situation> os = f.str2Obj(fen);
            assertThat(os).isPresent();
            assertThat(os.get().getBoard().visual()).isEqualTo(g.situation().getBoard().visual());
        }

        @Test
        @DisplayName("new game")
        void newGame() {
            compare(List.of(), "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        }

        @Test
        @DisplayName("one move")
        void oneMove() {
            compare(moves.stream().limit(1).toList(), "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        }

        @Test
        @DisplayName("2 moves")
        void twoMoves() {
            compare(moves.stream().limit(2).toList(), "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2");
        }

        @Test
        @DisplayName("3 moves")
        void threeMoves() {
            compare(moves.stream().limit(3).toList(), "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
        }

        @Test
        @DisplayName("4 moves")
        void fourMoves() {
            compare(moves.stream().limit(4).toList(), "rnbqkb1r/pp1ppppp/7n/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3");
        }

        @Test
        @DisplayName("5 moves")
        void fiveMoves() {
            compare(moves.stream().limit(5).toList(), "rnbqkb1r/pp1ppppp/7n/2p5/4P3/P4N2/1PPP1PPP/RNBQKB1R b KQkq - 0 3");
        }
    }
}

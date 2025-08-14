package leo.lija.chess.format;

import leo.lija.chess.Game;
import leo.lija.chess.Pos;
import leo.lija.chess.RichGame;
import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A3;
import static leo.lija.chess.Pos.C5;
import static leo.lija.chess.Pos.C7;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.F3;
import static leo.lija.chess.Pos.G1;
import static leo.lija.chess.Pos.G8;
import static leo.lija.chess.Pos.H6;
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
        List<Pair<Pos, Pos>> moves = List.of(Pair.of(E2, E4), Pair.of(C7, C5), Pair.of(G1, F3), Pair.of(G8, H6), Pair.of(A2, A3));

        @Test
        @DisplayName("new game")
        void newGame() {
            assertThat(f.str2Obj("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")).isEqualTo(new Game());
        }

        @Test
        @DisplayName("one move")
        void oneMove() {
            assertThat(f.str2Obj("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1").situation())
                .isEqualTo(new RichGame().playMoveList(moves.stream().limit(1).toList()).situation());
        }

        @Test
        @DisplayName("2 moves")
        void twoMoves() {
            assertThat(f.str2Obj("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2").situation())
                .isEqualTo(new RichGame().playMoveList(moves.stream().limit(2).toList()).situation());
        }

        @Test
        @DisplayName("3 moves")
        void threeMoves() {
            assertThat(f.str2Obj("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2").situation())
                .isEqualTo(new RichGame().playMoveList(moves.stream().limit(3).toList()).situation());
        }

        @Test
        @DisplayName("4 moves")
        void fourMoves() {
            assertThat(f.str2Obj("rnbqkb1r/pp1ppppp/7n/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3").situation())
                .isEqualTo(new RichGame().playMoveList(moves.stream().limit(4).toList()).situation());
        }

        @Test
        @DisplayName("5 moves")
        void fiveMoves() {
            assertThat(f.str2Obj("rnbqkb1r/pp1ppppp/7n/2p5/4P3/P4N2/1PPP1PPP/RNBQKB1R b KQkq - 0 3").situation())
                .isEqualTo(new RichGame().playMoveList(moves.stream().limit(5).toList()).situation());
        }
    }
}

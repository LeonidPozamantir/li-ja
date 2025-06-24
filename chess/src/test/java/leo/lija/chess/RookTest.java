package leo.lija.chess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Pos.A4;
import static leo.lija.chess.Pos.A6;
import static leo.lija.chess.Pos.A8;
import static leo.lija.chess.Pos.B4;
import static leo.lija.chess.Pos.B8;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.C4;
import static leo.lija.chess.Pos.C5;
import static leo.lija.chess.Pos.C6;
import static leo.lija.chess.Pos.C7;
import static leo.lija.chess.Pos.C8;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.D8;
import static leo.lija.chess.Pos.E1;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E3;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.E5;
import static leo.lija.chess.Pos.E6;
import static leo.lija.chess.Pos.E7;
import static leo.lija.chess.Pos.E8;
import static leo.lija.chess.Pos.F4;
import static leo.lija.chess.Pos.F8;
import static leo.lija.chess.Pos.G4;
import static leo.lija.chess.Pos.G8;
import static leo.lija.chess.Pos.H1;
import static leo.lija.chess.Pos.H2;
import static leo.lija.chess.Pos.H3;
import static leo.lija.chess.Pos.H4;
import static leo.lija.chess.Pos.H5;
import static leo.lija.chess.Pos.H6;
import static leo.lija.chess.Pos.H7;
import static leo.lija.chess.Pos.H8;
import static leo.lija.chess.Role.ROOK;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Rook should")
class RookTest extends BaseTest {

    private final Piece rook = new Piece(Color.WHITE, ROOK);

    @Test
    @DisplayName("move to any position along the same rank or file")
    void testBasicMoves() {
        assertThat(pieceMoves(rook, E4).get()).containsExactlyInAnyOrder(E5, E6, E7, E8, E3, E2, E1, F4, G4, H4, D4, C4, B4, A4);
    }

    @Test
    @DisplayName("move to any position along the same rank or file when in edge")
    void testBasicMovesEdge() {
        assertThat(pieceMoves(rook, H8).get()).containsExactlyInAnyOrder(H7, H6, H5, H4, H3, H2, H1, G8, F8, E8, D8, C8, B8, A8);
    }

    @Test
    @DisplayName("not move to positions that are occupied by the same color")
    void testOccupied() {
        Board board = visual.str2Obj("""
k B



N R    P

PPPPPPPP
 NBQKBNR
""");
        assertThat(board.destsFrom(C4).get()).containsExactlyInAnyOrder(C3, C5, C6, C7, B4, D4, E4, F4, G4);
    }

    @Test
    @DisplayName("capture enemy pieces")
    void testCapture() {
        Board board = visual.str2Obj("""
k
  b


n R   p

PPPPPPPP
 NBQKBNR
""");
        assertThat(board.destsFrom(C4).get()).containsExactlyInAnyOrder(C3, C5, C6, C7, B4, A4, D4, E4, F4, G4);
    }

    @Nested
    @DisplayName("threaten")
    class Threatens {
        Board board = visual.str2Obj("""
k B
  q  q
p

n R    P

PPPPPPPP
 NBQKBNR
""");

        @Test
        @DisplayName("reachable enemy to the left")
        void testReachableEnemyLeft() {
            assertThat(board.actorAt(C4).map(a -> a.threatens(A4))).contains(true);
        }

        @Test
        @DisplayName("reachable enemy to the top")
        void testReachableEnemyTop() {
            assertThat(board.actorAt(C4).map(a -> a.threatens(C7))).contains(true);
        }

        @Test
        @DisplayName("Unreachable enemy")
        void testUnreachableEnemy() {
            assertThat(board.actorAt(C4).map(a -> a.threatens(A6))).contains(false);
        }

        @Test
        @DisplayName("reachable friend")
        void testReachableFriend() {
            assertThat(board.actorAt(C4).map(a -> a.threatens(H4))).contains(false);
        }

        @Test
        @DisplayName("nothing left")
        void testNothingUpLeft() {
            assertThat(board.actorAt(C4).map(a -> a.threatens(B4))).contains(false);
        }

        @Test
        @DisplayName("nothing up")
        void testNothingDownRight() {
            assertThat(board.actorAt(C4).map(a -> a.threatens(C5))).contains(false);
        }
    }

}

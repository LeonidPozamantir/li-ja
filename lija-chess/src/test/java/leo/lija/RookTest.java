package leo.lija;

import leo.lija.format.VisualFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static leo.lija.Pos.A4;
import static leo.lija.Pos.A6;
import static leo.lija.Pos.A8;
import static leo.lija.Pos.B4;
import static leo.lija.Pos.B8;
import static leo.lija.Pos.C3;
import static leo.lija.Pos.C4;
import static leo.lija.Pos.C5;
import static leo.lija.Pos.C6;
import static leo.lija.Pos.C7;
import static leo.lija.Pos.C8;
import static leo.lija.Pos.D4;
import static leo.lija.Pos.D8;
import static leo.lija.Pos.E1;
import static leo.lija.Pos.E2;
import static leo.lija.Pos.E3;
import static leo.lija.Pos.E4;
import static leo.lija.Pos.E5;
import static leo.lija.Pos.E6;
import static leo.lija.Pos.E7;
import static leo.lija.Pos.E8;
import static leo.lija.Pos.F4;
import static leo.lija.Pos.F8;
import static leo.lija.Pos.G4;
import static leo.lija.Pos.G8;
import static leo.lija.Pos.H1;
import static leo.lija.Pos.H2;
import static leo.lija.Pos.H3;
import static leo.lija.Pos.H4;
import static leo.lija.Pos.H5;
import static leo.lija.Pos.H6;
import static leo.lija.Pos.H7;
import static leo.lija.Pos.H8;
import static leo.lija.Role.ROOK;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Rook should")
class RookTest {

    VisualFormat visual = new VisualFormat();

    private final Piece rook = new Piece(Color.WHITE, ROOK);
    private List<Pos> moves(Pos pos) {
        return Board.empty().placeAt(rook, pos).destsFrom(pos);
    }

    @Test
    @DisplayName("move to any position along the same rank or file")
    void testBasicMoves() {
        assertThat(moves(E4)).containsExactlyInAnyOrder(E5, E6, E7, E8, E3, E2, E1, F4, G4, H4, D4, C4, B4, A4);
    }

    @Test
    @DisplayName("move to any position along the same rank or file when in edge")
    void testBasicMovesEdge() {
        assertThat(moves(H8)).containsExactlyInAnyOrder(H7, H6, H5, H4, H3, H2, H1, G8, F8, E8, D8, C8, B8, A8);
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
        assertThat(board.destsFrom(C4)).containsExactlyInAnyOrder(C3, C5, C6, C7, B4, D4, E4, F4, G4);
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
        assertThat(board.destsFrom(C4)).containsExactlyInAnyOrder(C3, C5, C6, C7, B4, A4, D4, E4, F4, G4);
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
            assertThat(board.actorAt(C4).threatens(A4)).isTrue();
        }

        @Test
        @DisplayName("reachable enemy to the top")
        void testReachableEnemyTop() {
            assertThat(board.actorAt(C4).threatens(C7)).isTrue();
        }

        @Test
        @DisplayName("Unreachable enemy")
        void testUnreachableEnemy() {
            assertThat(board.actorAt(C4).threatens(A6)).isFalse();
        }

        @Test
        @DisplayName("reachable friend")
        void testReachableFriend() {
            assertThat(board.actorAt(C4).threatens(H4)).isFalse();
        }

        @Test
        @DisplayName("nothing left")
        void testNothingUpLeft() {
            assertThat(board.actorAt(C4).threatens(B4)).isFalse();
        }

        @Test
        @DisplayName("nothing up")
        void testNothingDownRight() {
            assertThat(board.actorAt(C4).threatens(C5)).isFalse();
        }
    }

}

package leo.lija;

import leo.lija.format.VisualFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static leo.lija.Pos.A2;
import static leo.lija.Pos.A6;
import static leo.lija.Pos.A8;
import static leo.lija.Pos.B1;
import static leo.lija.Pos.B5;
import static leo.lija.Pos.B7;
import static leo.lija.Pos.C2;
import static leo.lija.Pos.C4;
import static leo.lija.Pos.C6;
import static leo.lija.Pos.C7;
import static leo.lija.Pos.D3;
import static leo.lija.Pos.D5;
import static leo.lija.Pos.E4;
import static leo.lija.Pos.F3;
import static leo.lija.Pos.F5;
import static leo.lija.Pos.G2;
import static leo.lija.Pos.G6;
import static leo.lija.Pos.G8;
import static leo.lija.Pos.H1;
import static leo.lija.Pos.H7;
import static leo.lija.Role.BISHOP;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Bishop should")
class BishopTest {

	VisualFormat visual = new VisualFormat();

	private final Piece bishop = new Piece(Color.WHITE, BISHOP);
	private Set<Pos> moves(Pos pos) {
		return Board.empty().placeAt(bishop, pos).actorAt(pos).destinations();
	}

	@Test
	@DisplayName("move to any position along the diagonals")
	void testBasicMoves() {
		assertThat(moves(E4)).containsExactlyInAnyOrder(F3, G2, H1, D5, C6, B7, A8, D3, C2, B1, F5, G6, H7);
	}

	@Test
	@DisplayName("move to any position along the diagonals when on edge")
	void testBasicMovesOnEdge() {
		assertThat(moves(H7)).containsExactlyInAnyOrder(G8, G6, F5, E4, D3, C2, B1);
	}

	@Test
	@DisplayName("not move to positions that are occupied by the same color")
	void testOccupied() {
		Board board = visual.str2Obj("""
k B



N B    P

PPPPPPPP
 NBQKBNR
""");
		Set<Pos> possibleMoves = board.destsFrom(C4);
		assertThat(visual.newLine(visual.obj2StrWithMarks(board, Map.of(possibleMoves, 'x')))).isEqualTo("""
k B   x
     x
x   x
 x x
N B    P
 x x
PPPPPPPP
 NBQKBNR
""");
	}

	@Test
	@DisplayName("capture enemy pieces")
	void testCapture() {
		Board board = visual.str2Obj("""
k B
     q
p

N B    P

PPPPPPPP
 NBQKBNR
""");
		Set<Pos> possibleMoves = board.destsFrom(C4);
		assertThat(visual.newLine(visual.obj2StrWithMarks(board, Map.of(possibleMoves, 'x')))).isEqualTo("""
k B
     x
x   x
 x x
N B    P
 x x
PPPPPPPP
 NBQKBNR
""");
	}

	@Nested
	@DisplayName("threaten")
	class Threatens {
		Board board = visual.str2Obj("""
k B
  q  q
p

N B    P

PPPPPPPP
 NBQKBNR
""");

		@Test
		@DisplayName("reachable enemy")
		void testReachableEnemy() {
			assertThat(board.actorAt(C4).threatens(A6)).isTrue();
		}

		@Test
		@DisplayName("Unreachable enemy")
		void testUnreachableEnemy() {
			assertThat(board.actorAt(C4).threatens(C7)).isFalse();
		}

		@Test
		@DisplayName("reachable friend")
		void testReachableFriend() {
			assertThat(board.actorAt(C4).threatens(A2)).isFalse();
		}

		@Test
		@DisplayName("nothing up left")
		void testNothingUpLeft() {
			assertThat(board.actorAt(C4).threatens(B5)).isFalse();
		}

		@Test
		@DisplayName("nothing down right")
		void testNothingDownRight() {
			assertThat(board.actorAt(C4).threatens(D3)).isFalse();
		}
	}
}

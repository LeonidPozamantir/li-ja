package leo.lija;

import leo.lija.format.VisualFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static leo.lija.Pos.A1;
import static leo.lija.Pos.A4;
import static leo.lija.Pos.A6;
import static leo.lija.Pos.A7;
import static leo.lija.Pos.A8;
import static leo.lija.Pos.B2;
import static leo.lija.Pos.B4;
import static leo.lija.Pos.B6;
import static leo.lija.Pos.B8;
import static leo.lija.Pos.C2;
import static leo.lija.Pos.C3;
import static leo.lija.Pos.C4;
import static leo.lija.Pos.C5;
import static leo.lija.Pos.C8;
import static leo.lija.Pos.D1;
import static leo.lija.Pos.D2;
import static leo.lija.Pos.D3;
import static leo.lija.Pos.D4;
import static leo.lija.Pos.D5;
import static leo.lija.Pos.D6;
import static leo.lija.Pos.D7;
import static leo.lija.Pos.D8;
import static leo.lija.Pos.E3;
import static leo.lija.Pos.E4;
import static leo.lija.Pos.E5;
import static leo.lija.Pos.E8;
import static leo.lija.Pos.F2;
import static leo.lija.Pos.F4;
import static leo.lija.Pos.F6;
import static leo.lija.Pos.F8;
import static leo.lija.Pos.G1;
import static leo.lija.Pos.G4;
import static leo.lija.Pos.G7;
import static leo.lija.Pos.G8;
import static leo.lija.Pos.H1;
import static leo.lija.Pos.H2;
import static leo.lija.Pos.H3;
import static leo.lija.Pos.H4;
import static leo.lija.Pos.H5;
import static leo.lija.Pos.H6;
import static leo.lija.Pos.H7;
import static leo.lija.Pos.H8;
import static leo.lija.Role.QUEEN;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Queen should")
class QueenTest {

	VisualFormat visual = new VisualFormat();

	private final Piece queen = new Piece(Color.WHITE, QUEEN);
	private Set<Pos> moves(Pos pos) {
		return Board.empty().placeAt(queen, pos).destsFrom(pos);
	}

	@Test
	@DisplayName("move in any direction until the edge of the board")
	void testBasicMoves() {
		assertThat(moves(D4)).containsExactlyInAnyOrder(D5, D6, D7, D8, D3, D2, D1, E4, F4, G4, H4, C4, B4, A4, C3, B2, A1, E5, F6, G7, H8, C5, B6, A7, E3, F2, G1);
	}

	@Test
	@DisplayName("move in any direction when on edge")
	void testBasicMovesOnEdge() {
		assertThat(moves(H8)).containsExactlyInAnyOrder(H7, H6, H5, H4, H3, H2, H1, G7, F6, E5, D4, C3, B2, A1, G8, F8, E8, D8, C8, B8, A8);
	}

	@Test
	@DisplayName("not move to positions that are occupied by the same color")
	void testOccupied() {
		Board board = visual.str2Obj("""
k B



N Q    P

PPPPPPPP
 NBQKBNR
""");
		Set<Pos> possibleMoves = board.destsFrom(C4);
		assertThat(visual.newLine(visual.obj2StrWithMarks(board, Map.of(possibleMoves, 'x')))).isEqualTo("""
k B   x
  x  x
x x x
 xxx
NxQxxxxP
 xxx
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

N QP   P

PPPPPPPP
 NBQKBNR
""");
		Set<Pos> possibleMoves = board.destsFrom(C4);
		assertThat(visual.newLine(visual.obj2StrWithMarks(board, Map.of(possibleMoves, 'x')))).isEqualTo("""
k B
  x  x
x x x
 xxx
NxQP   P
 xxx
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

			n Q   Pp

			PPPPPPPP
			 NBQKBNR
			""");

		@Test
		@DisplayName("reachable enemy - horizontal")
		void testReachableEnemyHorizontal() {
			assertThat(board.actorAt(C4).threatens(A4)).isTrue();
		}

		@Test
		@DisplayName("reachable enemy - diagonal")
		void testReachableEnemyVertical() {
			assertThat(board.actorAt(C4).threatens(A6)).isTrue();
		}

		@Test
		@DisplayName("unreachable enemy")
		void testUnreachableEnemy() {
			assertThat(board.actorAt(C4).threatens(H4)).isFalse();
		}

		@Test
		@DisplayName("reachable friend")
		void testReachableFriend() {
			assertThat(board.actorAt(C4).threatens(C2)).isFalse();
		}

		@Test
		@DisplayName("Other fields")
		void testOtherFields() {
			assertThat(board.actorAt(C4).threatens(B6)).isFalse();
		}
	}
}

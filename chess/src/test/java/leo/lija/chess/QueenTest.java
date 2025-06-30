package leo.lija.chess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static leo.lija.chess.Pos.A1;
import static leo.lija.chess.Pos.A4;
import static leo.lija.chess.Pos.A6;
import static leo.lija.chess.Pos.A7;
import static leo.lija.chess.Pos.A8;
import static leo.lija.chess.Pos.B2;
import static leo.lija.chess.Pos.B4;
import static leo.lija.chess.Pos.B6;
import static leo.lija.chess.Pos.B8;
import static leo.lija.chess.Pos.C2;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.C4;
import static leo.lija.chess.Pos.C5;
import static leo.lija.chess.Pos.C8;
import static leo.lija.chess.Pos.D1;
import static leo.lija.chess.Pos.D2;
import static leo.lija.chess.Pos.D3;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.D5;
import static leo.lija.chess.Pos.D6;
import static leo.lija.chess.Pos.D7;
import static leo.lija.chess.Pos.D8;
import static leo.lija.chess.Pos.E3;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.E5;
import static leo.lija.chess.Pos.E8;
import static leo.lija.chess.Pos.F2;
import static leo.lija.chess.Pos.F4;
import static leo.lija.chess.Pos.F6;
import static leo.lija.chess.Pos.F8;
import static leo.lija.chess.Pos.G1;
import static leo.lija.chess.Pos.G4;
import static leo.lija.chess.Pos.G7;
import static leo.lija.chess.Pos.G8;
import static leo.lija.chess.Pos.H1;
import static leo.lija.chess.Pos.H2;
import static leo.lija.chess.Pos.H3;
import static leo.lija.chess.Pos.H4;
import static leo.lija.chess.Pos.H5;
import static leo.lija.chess.Pos.H6;
import static leo.lija.chess.Pos.H7;
import static leo.lija.chess.Pos.H8;
import static leo.lija.chess.Role.QUEEN;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Queen should")
class QueenTest extends BaseChess {

	private final Piece queen = new Piece(Color.WHITE, QUEEN);

	@Test
	@DisplayName("move in any direction until the edge of the board")
	void testBasicMoves() {
		assertThat(pieceMoves(queen, D4).get()).containsExactlyInAnyOrder(D5, D6, D7, D8, D3, D2, D1, E4, F4, G4, H4, C4, B4, A4, C3, B2, A1, E5, F6, G7, H8, C5, B6, A7, E3, F2, G1);
	}

	@Test
	@DisplayName("move in any direction when on edge")
	void testBasicMovesOnEdge() {
		assertThat(pieceMoves(queen, H8).get()).containsExactlyInAnyOrder(H7, H6, H5, H4, H3, H2, H1, G7, F6, E5, D4, C3, B2, A1, G8, F8, E8, D8, C8, B8, A8);
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
		List<Pos> possibleMoves = board.destsFrom(C4).get();
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
		List<Pos> possibleMoves = board.destsFrom(C4).get();
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
			assertThat(board.actorAt(C4).map(a -> a.threatens(A4))).contains(true);
		}

		@Test
		@DisplayName("reachable enemy - diagonal")
		void testReachableEnemyVertical() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(A6))).contains(true);
		}

		@Test
		@DisplayName("unreachable enemy")
		void testUnreachableEnemy() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(H4))).contains(false);
		}

		@Test
		@DisplayName("reachable friend")
		void testReachableFriend() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(C2))).contains(false);
		}

		@Test
		@DisplayName("Other fields")
		void testOtherFields() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(B6))).contains(false);
		}
	}
}

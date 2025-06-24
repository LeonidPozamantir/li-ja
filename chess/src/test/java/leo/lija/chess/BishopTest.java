package leo.lija.chess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A6;
import static leo.lija.chess.Pos.A8;
import static leo.lija.chess.Pos.B1;
import static leo.lija.chess.Pos.B5;
import static leo.lija.chess.Pos.B7;
import static leo.lija.chess.Pos.C2;
import static leo.lija.chess.Pos.C4;
import static leo.lija.chess.Pos.C6;
import static leo.lija.chess.Pos.C7;
import static leo.lija.chess.Pos.D3;
import static leo.lija.chess.Pos.D5;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.F3;
import static leo.lija.chess.Pos.F5;
import static leo.lija.chess.Pos.G2;
import static leo.lija.chess.Pos.G6;
import static leo.lija.chess.Pos.G8;
import static leo.lija.chess.Pos.H1;
import static leo.lija.chess.Pos.H7;
import static leo.lija.chess.Role.BISHOP;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Bishop should")
class BishopTest extends Base {

	private final Piece bishop = new Piece(Color.WHITE, BISHOP);

	@Test
	@DisplayName("move to any position along the diagonals")
	void testBasicMoves() {
		assertThat(pieceMoves(bishop, E4).get()).containsExactlyInAnyOrder(F3, G2, H1, D5, C6, B7, A8, D3, C2, B1, F5, G6, H7);
	}

	@Test
	@DisplayName("move to any position along the diagonals when on edge")
	void testBasicMovesOnEdge() {
		assertThat(pieceMoves(bishop, H7).get()).containsExactlyInAnyOrder(G8, G6, F5, E4, D3, C2, B1);
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
		List<Pos> possibleMoves = board.destsFrom(C4).get();
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
		List<Pos> possibleMoves = board.destsFrom(C4).get();
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
			assertThat(board.actorAt(C4).map(a -> a.threatens(A6))).contains(true);
		}

		@Test
		@DisplayName("Unreachable enemy")
		void testUnreachableEnemy() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(C7))).contains(false);
		}

		@Test
		@DisplayName("reachable friend")
		void testReachableFriend() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(A2))).contains(false);
		}

		@Test
		@DisplayName("nothing up left")
		void testNothingUpLeft() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(B5))).contains(false);
		}

		@Test
		@DisplayName("nothing down right")
		void testNothingDownRight() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(D3))).contains(false);
		}
	}
}

package leo.lija.chess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static leo.lija.chess.Pos.A5;
import static leo.lija.chess.Pos.A8;
import static leo.lija.chess.Pos.B4;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.C4;
import static leo.lija.chess.Pos.C5;
import static leo.lija.chess.Pos.D2;
import static leo.lija.chess.Pos.D6;
import static leo.lija.chess.Pos.E3;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.F2;
import static leo.lija.chess.Pos.F6;
import static leo.lija.chess.Pos.F7;
import static leo.lija.chess.Pos.G3;
import static leo.lija.chess.Pos.G5;
import static leo.lija.chess.Pos.G6;
import static leo.lija.chess.Pos.H8;
import static leo.lija.chess.Role.KNIGHT;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Knight should")
class KnightTest extends BaseChess {

	private final Piece knight = new Piece(Color.WHITE, KNIGHT);

	@Test
	@DisplayName("make L-shaped moves in any direction")
	void testBasicMoves() {
		assertThat(pieceMoves(knight, E4).get()).containsExactlyInAnyOrder(F6, G5, G3, F2, D2, C3, C5, D6);
	}

	@Test
	@DisplayName("make L-shaped moves in any direction when on edge")
	void testBasicMovesOnEdge() {
		assertThat(pieceMoves(knight, H8).get()).containsExactlyInAnyOrder(G6, F7);
	}

	@Test
	@DisplayName("not move to positions that are occupied by the same color")
	void testOccupied() {
		Board board = visual.str2Obj("""
k B

   B
    P
  N
    P
PPP  PPP
 NBQKBNR
""");
		List<Pos> possibleMoves = board.destsFrom(C4).get();
		assertThat(visual.newLine(visual.obj2StrWithMarks(board, Map.of(possibleMoves, 'x')))).isEqualTo("""
k B

 x B
x   P
  N
x   P
PPPx PPP
 NBQKBNR
""");
	}

	@Test
	@DisplayName("capture enemy pieces")
	void testCapture() {
		Board board = visual.str2Obj("""
k B

 b B
n
  N
    b
PPP  PPP
 NBQKBNR
""");
		List<Pos> possibleMoves = board.destsFrom(C4).get();
		assertThat(visual.newLine(visual.obj2StrWithMarks(board, Map.of(possibleMoves, 'x')))).isEqualTo("""
k B

 x B
x   x
  N
x   x
PPPx PPP
 NBQKBNR
""");
	}

	@Nested
	@DisplayName("threaten")
	class Threatens {
		Board board = visual.str2Obj("""
k B

 b B
n
  N
    Q
PPP  PPP
 NBQKBNR
""");

		@Test
		@DisplayName("reachable enemy")
		void testReachableEnemy() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(A5))).contains(true);
		}

		@Test
		@DisplayName("unreachable enemy")
		void testUnreachableEnemy() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(A8))).contains(false);
		}

		@Test
		@DisplayName("reachable friend")
		void testReachableFriend() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(E3))).contains(false);
		}

		@Test
		@DisplayName("nothing left")
		void testLeft() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(B4))).contains(false);
		}

		@Test
		@DisplayName("nothing up")
		void testUp() {
			assertThat(board.actorAt(C4).map(a -> a.threatens(C5))).contains(false);
		}
	}
}

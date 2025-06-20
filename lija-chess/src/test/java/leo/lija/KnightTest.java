package leo.lija;

import leo.lija.format.VisualFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static leo.lija.Pos.A5;
import static leo.lija.Pos.A8;
import static leo.lija.Pos.B4;
import static leo.lija.Pos.C3;
import static leo.lija.Pos.C4;
import static leo.lija.Pos.C5;
import static leo.lija.Pos.D2;
import static leo.lija.Pos.D6;
import static leo.lija.Pos.E3;
import static leo.lija.Pos.E4;
import static leo.lija.Pos.F2;
import static leo.lija.Pos.F6;
import static leo.lija.Pos.F7;
import static leo.lija.Pos.G3;
import static leo.lija.Pos.G5;
import static leo.lija.Pos.G6;
import static leo.lija.Pos.H8;
import static leo.lija.Role.KNIGHT;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Knight should")
class KnightTest {

	VisualFormat visual = new VisualFormat();

	private final Piece knight = new Piece(Color.WHITE, KNIGHT);
	private List<Pos> moves(Pos pos) {
		return Board.empty().placeAt(knight, pos).destsFrom(pos);
	}

	@Test
	@DisplayName("make L-shaped moves in any direction")
	void testBasicMoves() {
		assertThat(moves(E4)).containsExactlyInAnyOrder(F6, G5, G3, F2, D2, C3, C5, D6);
	}

	@Test
	@DisplayName("make L-shaped moves in any direction when on edge")
	void testBasicMovesOnEdge() {
		assertThat(moves(H8)).containsExactlyInAnyOrder(G6, F7);
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
		List<Pos> possibleMoves = board.destsFrom(C4);
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
		List<Pos> possibleMoves = board.destsFrom(C4);
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
			assertThat(board.actorAt(C4).threatens(A5)).isTrue();
		}

		@Test
		@DisplayName("unreachable enemy")
		void testUnreachableEnemy() {
			assertThat(board.actorAt(C4).threatens(A8)).isFalse();
		}

		@Test
		@DisplayName("reachable friend")
		void testReachableFriend() {
			assertThat(board.actorAt(C4).threatens(E3)).isFalse();
		}

		@Test
		@DisplayName("nothing left")
		void testLeft() {
			assertThat(board.actorAt(C4).threatens(B4)).isFalse();
		}

		@Test
		@DisplayName("nothing up")
		void testUp() {
			assertThat(board.actorAt(C4).threatens(C5)).isFalse();
		}
	}
}

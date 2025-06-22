package leo.lija.chess;

import leo.lija.chess.format.VisualFormat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static leo.lija.chess.Pos.A1;
import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A5;
import static leo.lija.chess.Pos.B1;
import static leo.lija.chess.Pos.B2;
import static leo.lija.chess.Pos.B5;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.C4;
import static leo.lija.chess.Pos.C5;
import static leo.lija.chess.Pos.D3;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.D5;
import static leo.lija.chess.Pos.E1;
import static leo.lija.chess.Pos.E3;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.E5;
import static leo.lija.chess.Pos.F1;
import static leo.lija.chess.Pos.G7;
import static leo.lija.chess.Pos.G8;
import static leo.lija.chess.Pos.H7;
import static leo.lija.chess.Pos.H8;
import static leo.lija.chess.Role.KING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("King should")
class KingTest {

	VisualFormat visual = new VisualFormat();

	private final Piece king = new Piece(Color.WHITE, KING);
	private List<Pos> moves(Pos pos) {
		return Board.empty().placeAt(king, pos).actorAt(pos).destinations();
	}

	@Test
	@DisplayName("move 1 position in any direction")
	void testBasicMoves() {
		assertThat(moves(D4)).containsExactlyInAnyOrder(D3, C3, C4, C5, D5, E5, E4, E3);
	}

	@Test
	@DisplayName("move 1 position in any direction when on edge")
	void testBasicMovesOnEdge() {
		assertThat(moves(H8)).containsExactlyInAnyOrder(H7, G7, G8);
	}

	@Test
	@DisplayName("move behind pawn barrier")
	void testBehindPawnBarrier() {
		Board board = visual.str2Obj("""
PPPPPPPP
R  QK NR""");
		assertThat(board.destsFrom(E1)).containsExactly(F1);
	}

	@Test
	@DisplayName("not move to positions that are occupied by the same color")
	@Disabled("two white kings")
	void testOccupied() {
		Board board = visual.str2Obj("""
   P
NPKP   P

PPPPPPPP
 NBQKBNR
""");
		List<Pos> possibleMoves = board.destsFrom(C4);
		assertThat(visual.newLine(visual.obj2StrWithMarks(board, Map.of(possibleMoves, 'x')))).isEqualTo("""



 xxP
NPKP   P
 xxx
PPPPPPPP
 NBQKBNR
""");
	}

	@Test
	@DisplayName("capture hanging opponent pieces")
	void testCapture() {
		Board board = visual.str2Obj("""
 bpp   k
  Kp
 p
N
""");
		List<Pos> possibleMoves = board.destsFrom(C3);
		assertThat(visual.newLine(visual.obj2StrWithMarks(board, Map.of(possibleMoves, 'x')))).isEqualTo("""




 xxx   k
  Kp
 x
N
""");
	}

	@Nested
	@DisplayName("threaten")
	class testThreatenNothing {
		Board board = visual.str2Obj("""
k B

 b B
bpp
  Kb
  P Q
PP   PPP
 NBQ BNR
""");

		@Test
		@DisplayName("reachable enemy")
		void testReachableEnemy() {
			assertThat(board.actorAt(C4).threatens(B5)).isTrue();
		}

		@Test
		@DisplayName("Unreachable enemy")
		void testUnreachableEnemy() {
			assertThat(board.actorAt(C4).threatens(A5)).isFalse();
		}

		@Test
		@DisplayName("reachable friend")
		void testReachableFriend() {
			assertThat(board.actorAt(C4).threatens(C3)).isFalse();
		}

	}

	@Test
	@DisplayName("not move near the other king")
	void testNearOtherKing() {
		assertThat(visual.str2Obj("""
   k
 K
""").destsFrom(B1)).containsExactlyInAnyOrder(A1, A2, B2);
	}
}

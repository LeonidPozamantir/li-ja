package leo.lija.model;

import leo.lija.format.VisualFormat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static leo.lija.model.Pos.A1;
import static leo.lija.model.Pos.A2;
import static leo.lija.model.Pos.A5;
import static leo.lija.model.Pos.B1;
import static leo.lija.model.Pos.B2;
import static leo.lija.model.Pos.B5;
import static leo.lija.model.Pos.C3;
import static leo.lija.model.Pos.C4;
import static leo.lija.model.Pos.C5;
import static leo.lija.model.Pos.D3;
import static leo.lija.model.Pos.D4;
import static leo.lija.model.Pos.D5;
import static leo.lija.model.Pos.E1;
import static leo.lija.model.Pos.E3;
import static leo.lija.model.Pos.E4;
import static leo.lija.model.Pos.E5;
import static leo.lija.model.Pos.F1;
import static leo.lija.model.Pos.G7;
import static leo.lija.model.Pos.G8;
import static leo.lija.model.Pos.H7;
import static leo.lija.model.Pos.H8;
import static leo.lija.model.Role.KING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("King should")
class KingTest {

	VisualFormat visual = new VisualFormat();

	private final Piece king = new Piece(Color.WHITE, KING);
	private Set<Pos> moves(Pos pos) {
		return Board.empty().placeAt(king, pos).actorAt(pos).moves();
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
		assertThat(board.movesFrom(E1)).containsExactly(F1);
	}

	@Test
	@DisplayName("not move to positions that are occupied by the same color")
	void testOccupied() {
		Board board = visual.str2Obj("""
k B


   P
NPKP   P

PPPPPPPP
 NBQKBNR
""");
		Set<Pos> possibleMoves = board.movesFrom(C4);
		assertThat(visual.newLine(visual.obj2StrWithMarks(board, Map.of(possibleMoves, 'x')))).isEqualTo("""
k B


 xxP
NPKP   P
 xxx
PPPPPPPP
 NBQKBNR
""");
	}

	@Test
	@Disabled("incorrect expectation")
	@DisplayName("capture enemy pieces")
	void testCapture() {
		Board board = visual.str2Obj("""
k B


  pP
NPKp   P
 p
PPPPPPPP
 NBQ BNR
""");
		Set<Pos> possibleMoves = board.movesFrom(C4);
		assertThat(visual.newLine(visual.obj2StrWithMarks(board, Map.of(possibleMoves, 'x')))).isEqualTo("""
k B


 xxP
NPKx   P
 x x
PPPPPPPP
 NBQ BNR
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
""").movesFrom(B1)).containsExactlyInAnyOrder(A1, A2, B2);
	}
}

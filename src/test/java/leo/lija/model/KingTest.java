package leo.lija.model;

import leo.lija.format.Visual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static leo.lija.model.Pos.C3;
import static leo.lija.model.Pos.C4;
import static leo.lija.model.Pos.C5;
import static leo.lija.model.Pos.D3;
import static leo.lija.model.Pos.D4;
import static leo.lija.model.Pos.D5;
import static leo.lija.model.Pos.E3;
import static leo.lija.model.Pos.E4;
import static leo.lija.model.Pos.E5;
import static leo.lija.model.Pos.G7;
import static leo.lija.model.Pos.G8;
import static leo.lija.model.Pos.H7;
import static leo.lija.model.Pos.H8;
import static leo.lija.model.Role.KING;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("King should")
class KingTest {

	Visual visual = new Visual();

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
	@DisplayName("capture enemy pieces")
	void testCapture() {
		Board board = visual.str2Obj("""
k B


  pP
NPKp   P
 p
PPPPPPPP
 NBQKBNR
""");
		Set<Pos> possibleMoves = board.movesFrom(C4);
		assertThat(visual.newLine(visual.obj2StrWithMarks(board, Map.of(possibleMoves, 'x')))).isEqualTo("""
k B


 xxP
NPKx   P
 xxx
PPPPPPPP
 NBQKBNR
""");
	}

	@Test
	@DisplayName("threaten nothing")
	void testThreatenNothing() {
		Board board = visual.str2Obj("""
k B

 b B
bpp
  Kb
    Q
PPP  PPP
 NBQ BNR
""");
		assertThat(Pos.all()).noneMatch(pos -> board.actorAt(C4).threatens(pos));
	}
}

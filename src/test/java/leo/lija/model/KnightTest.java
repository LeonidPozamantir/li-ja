package leo.lija.model;

import leo.lija.format.Visual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static leo.lija.model.Pos.C3;
import static leo.lija.model.Pos.C4;
import static leo.lija.model.Pos.C5;
import static leo.lija.model.Pos.D2;
import static leo.lija.model.Pos.D6;
import static leo.lija.model.Pos.E4;
import static leo.lija.model.Pos.F2;
import static leo.lija.model.Pos.F6;
import static leo.lija.model.Pos.F7;
import static leo.lija.model.Pos.G3;
import static leo.lija.model.Pos.G5;
import static leo.lija.model.Pos.G6;
import static leo.lija.model.Pos.H8;
import static leo.lija.model.Role.KNIGHT;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Knight should")
class KnightTest {

	Visual visual = new Visual();

	private final Piece knight = new Piece(Color.WHITE, KNIGHT);
	private Set<Pos> moves(Pos pos) {
		return Board.empty().placeAt(knight, pos).movesFrom(pos);
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
		Set<Pos> possibleMoves = board.movesFrom(C4);
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
b
  N
    q
PPP  PPP
 NBQKBNR
""");
		Set<Pos> possibleMoves = board.movesFrom(C4);
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
}

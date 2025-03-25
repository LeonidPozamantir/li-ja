package leo.lija.model;

import leo.lija.format.Visual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static leo.lija.model.Pos.A1;
import static leo.lija.model.Pos.A4;
import static leo.lija.model.Pos.A7;
import static leo.lija.model.Pos.A8;
import static leo.lija.model.Pos.B2;
import static leo.lija.model.Pos.B4;
import static leo.lija.model.Pos.B6;
import static leo.lija.model.Pos.B8;
import static leo.lija.model.Pos.C3;
import static leo.lija.model.Pos.C4;
import static leo.lija.model.Pos.C5;
import static leo.lija.model.Pos.C8;
import static leo.lija.model.Pos.D1;
import static leo.lija.model.Pos.D2;
import static leo.lija.model.Pos.D3;
import static leo.lija.model.Pos.D4;
import static leo.lija.model.Pos.D5;
import static leo.lija.model.Pos.D6;
import static leo.lija.model.Pos.D7;
import static leo.lija.model.Pos.D8;
import static leo.lija.model.Pos.E3;
import static leo.lija.model.Pos.E4;
import static leo.lija.model.Pos.E5;
import static leo.lija.model.Pos.E8;
import static leo.lija.model.Pos.F2;
import static leo.lija.model.Pos.F4;
import static leo.lija.model.Pos.F6;
import static leo.lija.model.Pos.F8;
import static leo.lija.model.Pos.G1;
import static leo.lija.model.Pos.G4;
import static leo.lija.model.Pos.G7;
import static leo.lija.model.Pos.G8;
import static leo.lija.model.Pos.H1;
import static leo.lija.model.Pos.H2;
import static leo.lija.model.Pos.H3;
import static leo.lija.model.Pos.H4;
import static leo.lija.model.Pos.H5;
import static leo.lija.model.Pos.H6;
import static leo.lija.model.Pos.H7;
import static leo.lija.model.Pos.H8;
import static leo.lija.model.Role.QUEEN;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Queen should")
class QueenTest {

	Visual visual = new Visual();

	private final Piece queen = new Piece(Color.WHITE, QUEEN);
	private Set<Pos> moves(Pos pos) {
		return Board.empty().placeAt(queen, pos).movesFrom(pos);
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
		Set<Pos> possibleMoves = board.movesFrom(C4);
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
		Set<Pos> possibleMoves = board.movesFrom(C4);
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
}

package leo.lija.model;

import leo.lija.format.Visual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static leo.lija.model.Pos.A6;
import static leo.lija.model.Pos.A8;
import static leo.lija.model.Pos.B1;
import static leo.lija.model.Pos.B7;
import static leo.lija.model.Pos.C2;
import static leo.lija.model.Pos.C4;
import static leo.lija.model.Pos.C6;
import static leo.lija.model.Pos.D3;
import static leo.lija.model.Pos.D5;
import static leo.lija.model.Pos.E4;
import static leo.lija.model.Pos.F3;
import static leo.lija.model.Pos.F5;
import static leo.lija.model.Pos.G2;
import static leo.lija.model.Pos.G6;
import static leo.lija.model.Pos.G8;
import static leo.lija.model.Pos.H1;
import static leo.lija.model.Pos.H7;
import static leo.lija.model.Role.BISHOP;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Bishop should")
class BishopTest {

	Visual visual = new Visual();

	private final Piece bishop = new Piece(Color.WHITE, BISHOP);
	private Set<Pos> moves(Pos pos) {
		return Board.empty().placeAt(bishop, pos).actorAt(pos).moves();
	}

	@Test
	@DisplayName("move to any position along the diagonals")
	void testBasicMoves() {
		assertThat(moves(E4)).containsExactlyInAnyOrder(F3, G2, H1, D5, C6, B7, A8, D3, C2, B1, F5, G6, H7);
	}

	@Test
	@DisplayName("move to any position along the diagonals when on edge")
	void testBasicMovesOnEdge() {
		assertThat(moves(H7)).containsExactlyInAnyOrder(G8, G6, F5, E4, D3, C2, B1);
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
		Set<Pos> possibleMoves = board.movesFrom(C4);
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
		Set<Pos> possibleMoves = board.movesFrom(C4);
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

	@Test
	@DisplayName("threaten enemies")
	void testThreatensEnemies() {
		assertThat(visual.str2Obj("""
k B
     q
p

N B    P

PPPPPPPP
 NBQKBNR
""").actorAt(C4).threatens(A6)).isTrue();
	}
}

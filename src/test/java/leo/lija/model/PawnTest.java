package leo.lija.model;

import leo.lija.format.Visual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Pos.A3;
import static leo.lija.model.Pos.A4;
import static leo.lija.model.Pos.A5;
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
import static leo.lija.model.Role.PAWN;
import static org.assertj.core.api.Assertions.assertThat;


public class PawnTest {

	Visual visual = new Visual();

	@Nested
	@DisplayName("White pawn should")
	class WhitePawnTest {

		private final Piece pawn = new Piece(WHITE, PAWN);

		private Set<Pos> basicMoves(Pos pos) {
			return pawn.basicMoves(pos, Board.empty().placeAt(pawn, pos));
		}

		@Test
		@DisplayName("move 1 square forward")
		void testBasicMoves() {
			assertThat(basicMoves(A4)).containsExactly(A5);
		}

		@Test
		@DisplayName("not move to positions that are occupied by the same color")
		void testOccupied() {
			Board board = Board.empty().placeAt(new Piece(WHITE, PAWN), A4).placeAt(new Piece(WHITE, PAWN), A5);
			Set<Pos> possibleMoves = board.pieceAt(A4).basicMoves(A4, board);
			assertThat(possibleMoves).isEmpty();
		}
	}

	@Nested
	@DisplayName("Black pawn should")
	class BlackPawnTest {

		private final Piece pawn = new Piece(BLACK, PAWN);

		private Set<Pos> basicMoves(Pos pos) {
			return pawn.basicMoves(pos, Board.empty().placeAt(pawn, pos));
		}

		@Test
		@DisplayName("move 1 square forward")
		void testBasicMoves() {
			assertThat(basicMoves(A4)).containsExactly(A3);
		}

		@Test
		@DisplayName("not move to positions that are occupied by the same color")
		void testOccupied() {
			Board board = Board.empty().placeAt(new Piece(BLACK, PAWN), A4).placeAt(new Piece(BLACK, PAWN), A3);
			Set<Pos> possibleMoves = board.pieceAt(A4).basicMoves(A4, board);
			assertThat(possibleMoves).isEmpty();
		}
	}
}

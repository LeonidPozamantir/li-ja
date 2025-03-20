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
import static leo.lija.model.Pos.C3;
import static leo.lija.model.Pos.C5;
import static leo.lija.model.Pos.D3;
import static leo.lija.model.Pos.D4;
import static leo.lija.model.Pos.D5;
import static leo.lija.model.Pos.E3;
import static leo.lija.model.Pos.E5;
import static leo.lija.model.Role.BISHOP;
import static leo.lija.model.Role.PAWN;
import static org.assertj.core.api.Assertions.assertThat;


public class PawnTest {

	Visual visual = new Visual();

	@Nested
	@DisplayName("White pawn should")
	class WhitePawnTest {

		@Test
		@DisplayName("move 1 square forward")
		void testBasicMoves() {
			assertThat(new Board(Map.of(
					A4, WHITE.pawn()
			)).basicMoves(A4)).containsExactly(A5);
		}

		@Test
		@DisplayName("not move to positions that are occupied by the same color")
		void testOccupied() {
			assertThat(new Board(Map.of(
					A4, WHITE.pawn(),
					A5, WHITE.pawn()
			)).basicMoves(A4)).isEmpty();
		}

		@Test
		@DisplayName("capture diagonally")
		void testCapture() {
			assertThat(new Board(Map.of(
					D4, WHITE.pawn(),
					C5, BLACK.pawn(),
					E5, BLACK.bishop()
			)).basicMoves(D4)).containsExactlyInAnyOrder(C5, D5, E5);
		}

		@Test
		@DisplayName("capture only diagonally")
		void testCaptureOnlyDiagonally() {
			assertThat(new Board(Map.of(
					A4, WHITE.pawn(),
					C5, WHITE.pawn()
			)).basicMoves(A4)).containsExactly(A5);
		}
	}

	@Nested
	@DisplayName("Black pawn should")
	class BlackPawnTest {

		@Test
		@DisplayName("move 1 square forward")
		void testBasicMoves() {
			assertThat(new Board(Map.of(
					A4, BLACK.pawn()
			)).basicMoves(A4)).containsExactly(A3);
		}

		@Test
		@DisplayName("not move to positions that are occupied by the same color")
		void testOccupied() {
			assertThat(new Board(Map.of(
					A4, BLACK.pawn(),
					A3, BLACK.pawn()
			)).basicMoves(A4)).isEmpty();
		}

		@Test
		@DisplayName("capture diagonally")
		void testCapture() {
			assertThat(new Board(Map.of(
					D4, BLACK.pawn(),
					C3, WHITE.pawn(),
					E3, WHITE.bishop()
			)).basicMoves(D4)).containsExactlyInAnyOrder(C3, D3, E3);
		}

		@Test
		@DisplayName("capture only diagonally")
		void testCaptureOnlyDiagonally() {
			assertThat(new Board(Map.of(
					A4, BLACK.pawn(),
					C3, BLACK.pawn()
			)).basicMoves(A4)).containsExactly(A3);
		}
	}
}

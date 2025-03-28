package leo.lija.model;

import leo.lija.format.Visual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Pos.A2;
import static leo.lija.model.Pos.A3;
import static leo.lija.model.Pos.A4;
import static leo.lija.model.Pos.A5;
import static leo.lija.model.Pos.A6;
import static leo.lija.model.Pos.A7;
import static leo.lija.model.Pos.C3;
import static leo.lija.model.Pos.C5;
import static leo.lija.model.Pos.C6;
import static leo.lija.model.Pos.C7;
import static leo.lija.model.Pos.D3;
import static leo.lija.model.Pos.D4;
import static leo.lija.model.Pos.D5;
import static leo.lija.model.Pos.D6;
import static leo.lija.model.Pos.E3;
import static leo.lija.model.Pos.E5;
import static leo.lija.model.Pos.E6;
import static leo.lija.model.Pos.E7;
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
			)).movesFrom(A4)).containsExactly(A5);
		}

		@Test
		@DisplayName("not move to positions that are occupied by the same color")
		void testOccupied() {
			assertThat(new Board(Map.of(
					A4, WHITE.pawn(),
					A5, WHITE.pawn()
			)).movesFrom(A4)).isEmpty();
		}

		@Test
		@DisplayName("capture diagonally")
		void testCapture() {
			assertThat(new Board(Map.of(
					D4, WHITE.pawn(),
					C5, BLACK.pawn(),
					E5, BLACK.bishop()
			)).movesFrom(D4)).containsExactlyInAnyOrder(C5, D5, E5);
		}

		@Test
		@DisplayName("capture only diagonally")
		void testCaptureOnlyDiagonally() {
			assertThat(new Board(Map.of(
					A4, WHITE.pawn(),
					C5, WHITE.pawn()
			)).movesFrom(A4)).containsExactly(A5);
		}

		@Nested
		@DisplayName("move forward two squares")
		class TwoSquaresTest {

			@Test
			@DisplayName("if the path is free")
			void pathFree() {
				assertThat(new Board(Map.of(
					A2, WHITE.pawn()
				)).movesFrom(A2)).containsExactly(A3, A4);
			}

			@Test
			@DisplayName("if the next square is occupied by a friend")
			void nextFriend() {
				assertThat(new Board(Map.of(
					A2, WHITE.pawn(),
					A3, WHITE.rook()
				)).movesFrom(A2)).isEmpty();
			}

			@Test
			@DisplayName("if the square two spaces away is occupied by a friend")
			void twoSpacesAwayFriend() {
				assertThat(new Board(Map.of(
					A2, WHITE.pawn(),
					A4, WHITE.rook()
				)).movesFrom(A2)).containsExactly(A3);
			}

			@Test
			@DisplayName("if the next square is occupied by an enemy")
			void nextEnemy() {
				assertThat(new Board(Map.of(
					A2, WHITE.pawn(),
					A3, BLACK.rook()
				)).movesFrom(A2)).isEmpty();
			}

			@Test
			@DisplayName("if the square two spaces away is occupied by an enemy")
			void twoSpacesAwayEnemy() {
				assertThat(new Board(Map.of(
					A2, WHITE.pawn(),
					A4, BLACK.rook()
				)).movesFrom(A2)).containsExactly(A3);
			}
		}

		@Nested
		@DisplayName("capture en passant")
		class EnPassant {

			@Nested
			@DisplayName("with proper position")
			class ProperPosition {
				Board board = new Board(Map.of(
					D5, WHITE.pawn(),
					C5, BLACK.pawn(),
					E5, BLACK.pawn()
				));

				@Test
				@DisplayName("without history")
				void withoutHistory() {
					assertThat(board.movesFrom(D5)).containsExactly(D6);
				}

				@Test
				@DisplayName("with irrelevant history")
				void withIrrelevantHistory() {
					assertThat(board.withHistory(new History(Optional.of(Pair.of(A2, A4)))).movesFrom(D5)).containsExactly(D6);
				}

				@Test
				@DisplayName("with relevant history on the left")
				void withRelevantHistoryLeft() {
					assertThat(board.withHistory(new History(Optional.of(Pair.of(C7, C5)))).movesFrom(D5)).containsExactlyInAnyOrder(D6, C6);
				}

				@Test
				@DisplayName("with relevant history on the right")
				void withRelevantHistoryRight() {
					assertThat(board.withHistory(new History(Optional.of(Pair.of(E7, E5)))).movesFrom(D5)).containsExactlyInAnyOrder(D6, E6);
				}
			}

			@Test
			@DisplayName("enemy not-a-pawn")
			void enemyNotPawn() {
				Board board = new Board(Map.of(
					D5, WHITE.pawn(),
					E5, BLACK.rook()
				), new History(Optional.of(Pair.of(E7, E5))));
				assertThat(board.movesFrom(D5)).containsExactly(D6);
			}

			@Test
			@DisplayName("friend pawn (?!)")
			void friendPawn() {
				Board board = new Board(Map.of(
					D5, WHITE.pawn(),
					E5, WHITE.pawn()
				), new History(Optional.of(Pair.of(E7, E5))));
				assertThat(board.movesFrom(D5)).containsExactly(D6);
			}
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
			)).movesFrom(A4)).containsExactly(A3);
		}

		@Test
		@DisplayName("not move to positions that are occupied by the same color")
		void testOccupied() {
			assertThat(new Board(Map.of(
					A4, BLACK.pawn(),
					A3, BLACK.pawn()
			)).movesFrom(A4)).isEmpty();
		}

		@Test
		@DisplayName("capture diagonally")
		void testCapture() {
			assertThat(new Board(Map.of(
					D4, BLACK.pawn(),
					C3, WHITE.pawn(),
					E3, WHITE.bishop()
			)).movesFrom(D4)).containsExactlyInAnyOrder(C3, D3, E3);
		}

		@Test
		@DisplayName("capture only diagonally")
		void testCaptureOnlyDiagonally() {
			assertThat(new Board(Map.of(
					A4, BLACK.pawn(),
					C3, BLACK.pawn()
			)).movesFrom(A4)).containsExactlyInAnyOrder(A3);
		}

		@Nested
		@DisplayName("move forward two squares")
		class TwoSquaresTest {

			@Test
			@DisplayName("if the path is free")
			void pathFree() {
				assertThat(new Board(Map.of(
					A7, BLACK.pawn()
				)).movesFrom(A7)).containsExactlyInAnyOrder(A6, A5);
			}

			@Test
			@DisplayName("if the next square is occupied by a friend")
			void nextFriend() {
				assertThat(new Board(Map.of(
					A7, BLACK.pawn(),
					A6, BLACK.rook()
				)).movesFrom(A7)).isEmpty();
			}

			@Test
			@DisplayName("if the square two spaces away is occupied by a friend")
			void twoSpacesAwayFriend() {
				assertThat(new Board(Map.of(
					A7, BLACK.pawn(),
					A5, BLACK.rook()
				)).movesFrom(A7)).containsExactly(A6);
			}

			@Test
			@DisplayName("if the next square is occupied by an enemy")
			void nextEnemy() {
				assertThat(new Board(Map.of(
					A7, BLACK.pawn(),
					A6, WHITE.rook()
				)).movesFrom(A7)).isEmpty();
			}

			@Test
			@DisplayName("if the square two spaces away is occupied by an enemy")
			void twoSpacesAwayEnemy() {
				assertThat(new Board(Map.of(
					A7, BLACK.pawn(),
					A5, WHITE.rook()
				)).movesFrom(A7)).containsExactly(A6);
			}
		}
	}
}

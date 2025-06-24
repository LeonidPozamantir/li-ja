package leo.lija.chess;

import leo.lija.chess.format.VisualFormat;
import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A3;
import static leo.lija.chess.Pos.A4;
import static leo.lija.chess.Pos.A5;
import static leo.lija.chess.Pos.A6;
import static leo.lija.chess.Pos.A7;
import static leo.lija.chess.Pos.C2;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.C4;
import static leo.lija.chess.Pos.C5;
import static leo.lija.chess.Pos.C6;
import static leo.lija.chess.Pos.C7;
import static leo.lija.chess.Pos.D3;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.D5;
import static leo.lija.chess.Pos.D6;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E3;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.E5;
import static leo.lija.chess.Pos.E6;
import static leo.lija.chess.Pos.E7;
import static org.assertj.core.api.Assertions.assertThat;


public class PawnTest {

	VisualFormat visual = new VisualFormat();

	@Nested
	@DisplayName("White pawn should")
	class WhitePawnTest {

		@Test
		@DisplayName("move 1 square forward")
		void testBasicMoves() {
			assertThat(new Board(Map.of(
					A4, WHITE.pawn()
			)).destsFrom(A4).get()).containsExactly(A5);
		}

		@Test
		@DisplayName("not move to positions that are occupied by the same color")
		void testOccupied() {
			assertThat(new Board(Map.of(
					A4, WHITE.pawn(),
					A5, WHITE.pawn()
			)).destsFrom(A4).get()).isEmpty();
		}

		@Test
		@DisplayName("capture diagonally")
		void testCapture() {
			assertThat(new Board(Map.of(
					D4, WHITE.pawn(),
					C5, BLACK.pawn(),
					E5, BLACK.bishop()
			)).destsFrom(D4).get()).containsExactlyInAnyOrder(C5, D5, E5);
		}

		@Test
		@DisplayName("capture only diagonally")
		void testCaptureOnlyDiagonally() {
			assertThat(new Board(Map.of(
					A4, WHITE.pawn(),
					C5, WHITE.pawn()
			)).destsFrom(A4).get()).containsExactly(A5);
		}

		@Nested
		@DisplayName("move forward two squares")
		class TwoSquaresTest {

			@Test
			@DisplayName("if the path is free")
			void pathFree() {
				assertThat(new Board(Map.of(
					A2, WHITE.pawn()
				)).destsFrom(A2).get()).containsExactly(A3, A4);
			}

			@Test
			@DisplayName("if the next square is occupied by a friend")
			void nextFriend() {
				assertThat(new Board(Map.of(
					A2, WHITE.pawn(),
					A3, WHITE.rook()
				)).destsFrom(A2).get()).isEmpty();
			}

			@Test
			@DisplayName("if the square two spaces away is occupied by a friend")
			void twoSpacesAwayFriend() {
				assertThat(new Board(Map.of(
					A2, WHITE.pawn(),
					A4, WHITE.rook()
				)).destsFrom(A2).get()).containsExactly(A3);
			}

			@Test
			@DisplayName("if the next square is occupied by an enemy")
			void nextEnemy() {
				assertThat(new Board(Map.of(
					A2, WHITE.pawn(),
					A3, BLACK.rook()
				)).destsFrom(A2).get()).isEmpty();
			}

			@Test
			@DisplayName("if the square two spaces away is occupied by an enemy")
			void twoSpacesAwayEnemy() {
				assertThat(new Board(Map.of(
					A2, WHITE.pawn(),
					A4, BLACK.rook()
				)).destsFrom(A2).get()).containsExactly(A3);
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
					assertThat(board.destsFrom(D5).get()).containsExactly(D6);
				}

				@Test
				@DisplayName("with irrelevant history")
				void withIrrelevantHistory() {
					assertThat(board.withHistory(new History(Optional.of(Pair.of(A2, A4)))).destsFrom(D5).get()).containsExactly(D6);
				}

				@Test
				@DisplayName("with relevant history on the left")
				void withRelevantHistoryLeft() {
					assertThat(board.withHistory(new History(Optional.of(Pair.of(C7, C5)))).destsFrom(D5).get()).containsExactlyInAnyOrder(D6, C6);
				}

				@Test
				@DisplayName("with relevant history on the right")
				void withRelevantHistoryRight() {
					assertThat(board.withHistory(new History(Optional.of(Pair.of(E7, E5)))).destsFrom(D5).get()).containsExactlyInAnyOrder(D6, E6);
				}
			}

			@Test
			@DisplayName("enemy not-a-pawn")
			void enemyNotPawn() {
				Board board = new Board(Map.of(
					D5, WHITE.pawn(),
					E5, BLACK.rook()
				), new History(Optional.of(Pair.of(E7, E5))));
				assertThat(board.destsFrom(D5).get()).containsExactly(D6);
			}

			@Test
			@DisplayName("friend pawn (?!)")
			void friendPawn() {
				Board board = new Board(Map.of(
					D5, WHITE.pawn(),
					E5, WHITE.pawn()
				), new History(Optional.of(Pair.of(E7, E5))));
				assertThat(board.destsFrom(D5).get()).containsExactly(D6);
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
			)).destsFrom(A4).get()).containsExactly(A3);
		}

		@Test
		@DisplayName("not move to positions that are occupied by the same color")
		void testOccupied() {
			assertThat(new Board(Map.of(
					A4, BLACK.pawn(),
					A3, BLACK.pawn()
			)).destsFrom(A4).get()).isEmpty();
		}

		@Test
		@DisplayName("capture diagonally")
		void testCapture() {
			assertThat(new Board(Map.of(
					D4, BLACK.pawn(),
					C3, WHITE.pawn(),
					E3, WHITE.bishop()
			)).destsFrom(D4).get()).containsExactlyInAnyOrder(C3, D3, E3);
		}

		@Test
		@DisplayName("capture only diagonally")
		void testCaptureOnlyDiagonally() {
			assertThat(new Board(Map.of(
					A4, BLACK.pawn(),
					C3, BLACK.pawn()
			)).destsFrom(A4).get()).containsExactlyInAnyOrder(A3);
		}

		@Nested
		@DisplayName("move forward two squares")
		class TwoSquaresTest {

			@Test
			@DisplayName("if the path is free")
			void pathFree() {
				assertThat(new Board(Map.of(
					A7, BLACK.pawn()
				)).destsFrom(A7).get()).containsExactlyInAnyOrder(A6, A5);
			}

			@Test
			@DisplayName("if the next square is occupied by a friend")
			void nextFriend() {
				assertThat(new Board(Map.of(
					A7, BLACK.pawn(),
					A6, BLACK.rook()
				)).destsFrom(A7).get()).isEmpty();
			}

			@Test
			@DisplayName("if the square two spaces away is occupied by a friend")
			void twoSpacesAwayFriend() {
				assertThat(new Board(Map.of(
					A7, BLACK.pawn(),
					A5, BLACK.rook()
				)).destsFrom(A7).get()).containsExactly(A6);
			}

			@Test
			@DisplayName("if the next square is occupied by an enemy")
			void nextEnemy() {
				assertThat(new Board(Map.of(
					A7, BLACK.pawn(),
					A6, WHITE.rook()
				)).destsFrom(A7).get()).isEmpty();
			}

			@Test
			@DisplayName("if the square two spaces away is occupied by an enemy")
			void twoSpacesAwayEnemy() {
				assertThat(new Board(Map.of(
					A7, BLACK.pawn(),
					A5, WHITE.rook()
				)).destsFrom(A7).get()).containsExactly(A6);
			}
		}

		@Nested
		@DisplayName("capture en passant")
		class EnPassant {

			@Nested
			@DisplayName("with proper position")
			class ProperPosition {
				Board board = new Board(Map.of(
					D4, BLACK.pawn(),
					C4, WHITE.pawn(),
					E4, WHITE.pawn()
				));

				@Test
				@DisplayName("without history")
				void withoutHistory() {
					assertThat(board.destsFrom(D4).get()).containsExactly(D3);
				}

				@Test
				@DisplayName("with relevant history on the left")
				void withRelevantHistoryLeft() {
					assertThat(board.withHistory(new History(Optional.of(Pair.of(C2, C4)))).destsFrom(D4).get()).containsExactlyInAnyOrder(D3, C3);
				}

			}

			@Test
			@DisplayName("enemy not-a-pawn")
			void enemyNotPawn() {
				Board board = new Board(Map.of(
					D4, BLACK.pawn(),
					E4, WHITE.rook()
				), new History(Optional.of(Pair.of(E2, E4))));
				assertThat(board.destsFrom(D4).get()).containsExactly(D3);
			}

		}
	}
}

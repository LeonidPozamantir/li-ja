package leo.lija.chess;

import leo.lija.chess.format.VisualFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Pos.D1;
import static leo.lija.chess.Pos.D2;
import static leo.lija.chess.Pos.D3;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.E1;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E3;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.F1;
import static leo.lija.chess.Pos.F2;
import static leo.lija.chess.Pos.F3;
import static leo.lija.chess.Pos.G1;
import static leo.lija.chess.Pos.G2;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("In order to save the king should")
class KingSafetyTest {

	VisualFormat visual = new VisualFormat();

	@Nested
	@DisplayName("the king")
	class King {
		@Test
		@DisplayName("not commit suicide")
		void notCommitSuicide() {
			Board board = visual.str2Obj("""
    P n
PPPP   P
RNBQK  R""");
			assertThat(board.destsFrom(E1)).containsExactly(F2);
		}

		@Test
		@DisplayName("not commit suicide even if immobilized")
		void notCommitSuicideIfImmobilized() {
			Board board = visual.str2Obj("""
    b n
PPPP   P
RNBQK  R""");
			assertThat(board.destsFrom(E1)).isEmpty();
		}

		@Test
		@DisplayName("escape from danger")
		void escapeFromDanger() {
			Board board = visual.str2Obj("""
    r

PPPP   P
RNBQK  R""");
			assertThat(board.destsFrom(E1)).containsExactlyInAnyOrder(F1, F2);
		}

	}

	@Nested
	@DisplayName("pieces")
	class Pieces {

		@Nested
		@DisplayName("move to defend")
		class MoveToDefend {
			@Test
			void queen() {
				Board board = visual.str2Obj("""
    r

PPPP   P
RNBQK  R""");
				assertThat(board.destsFrom(D1)).containsExactly(E2);
			}

			@Test
			void knight() {
				Board board = visual.str2Obj("""
    r

PPPP   P
RNBQK NR""");
				assertThat(board.destsFrom(G1)).containsExactly(E2);
			}

			@Test
			void pawn() {
				Board board = visual.str2Obj("""
  K    r
PPPP   P
RNBQ  NR""");
				assertThat(board.destsFrom(D2)).containsExactly(D3);
			}

			@Test
			@DisplayName("pawn double square")
			void pawnDoubleSquare() {
				Board board = visual.str2Obj("""
  K    r

PPPP   P
RNBQ  NR""");
				assertThat(board.destsFrom(D2)).containsExactly(D4);
			}
		}

		@Nested
		@DisplayName("eat to defend")
		class EatToDefend {
			@Test
			void queen() {
				Board board = visual.str2Obj("""
    r

PPPPK Q
RNB    R""");
				assertThat(board.destsFrom(G2)).containsExactly(E4);
			}

			@Test
			@DisplayName("queen defender")
			void queenDefender() {
				Board board = visual.str2Obj("""
    r

PPPPQ
RNB K  R""");
				assertThat(board.destsFrom(E2)).containsExactlyInAnyOrder(E3, E4);
			}

			@Test
			void pawn() {
				Board board = visual.str2Obj("""
    r
     P
PPPP
RNB K  R""");
				assertThat(board.destsFrom(F3)).containsExactly(E4);
			}
		}

		@Nested
		@DisplayName("stay to defend")
		class StayToDefend {
			@Test
			void bishop() {
				Board board = visual.str2Obj("""
    r

PPPPB
RNB K  R""");
				assertThat(board.destsFrom(E2)).isEmpty();
			}

			@Test
			void pawn() {
				Board board = visual.str2Obj("""

 K P  r
PPP
RNB    R""");
				assertThat(board.destsFrom(D3)).isEmpty();
			}
		}
	}
}

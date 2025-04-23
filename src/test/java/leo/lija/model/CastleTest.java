package leo.lija.model;

import leo.lija.format.VisualFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Pos.A1;
import static leo.lija.model.Pos.B1;
import static leo.lija.model.Pos.C1;
import static leo.lija.model.Pos.D1;
import static leo.lija.model.Pos.E1;
import static leo.lija.model.Pos.F1;
import static leo.lija.model.Pos.G1;
import static leo.lija.model.Pos.H1;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("a king should castle")
public class CastleTest {

	private void beSituation(Situation situation, String visualString) {
		assertThat(visual.obj2Str(situation.board))
			.isEqualTo(visual.obj2Str(visual.str2Obj(visualString)));
	}

	VisualFormat visual = new VisualFormat();
	@Nested
	@DisplayName("king side")
	class Kingside {

		Board goodHist = visual.str2Obj("""
PPPPPPPP
R  QK  R""");
		Board badHist = goodHist.updateHistory(h -> h.withoutCastles(WHITE));

		@Nested
		@DisplayName("impossible")
		class Impossible {

			@Nested
			@DisplayName("standard chess")
			class StandardChess {
				@Test
				@DisplayName("bishop in the way")
				void bishopInTheWay() {
					assertThat(goodHist.placeAt(WHITE.bishop(), F1).movesFrom(E1)).isEmpty();
				}
				@Test
				@DisplayName("knight in the way")
				void knightInTheWay() {
					assertThat(goodHist.placeAt(WHITE.knight(), G1).movesFrom(E1)).containsExactly(F1);
				}
				@Test
				@DisplayName("not allowed by history")
				void badHistory() {
					assertThat(badHist.movesFrom(E1)).containsExactly(F1);
				}
			}

			@Nested
			@DisplayName("chess960")
			class Chess960 {
				Board board960 = visual.str2Obj("""
PPPPPPPP
RQK   R """).withHistory(History.castle(WHITE, true, true));

				@Test
				@DisplayName("bishop in the way")
				void bishopInTheWay() {
					assertThat(board960.placeAt(WHITE.bishop(), D1).movesFrom(C1)).isEmpty();
				}
				@Test
				@DisplayName("knight in the way")
				void knightInTheWay() {
					assertThat(board960.placeAt(WHITE.knight(), F1).movesFrom(C1)).containsExactly(D1);
				}
			}

		}

		@Nested
		@DisplayName("possible")
		class Possible {
			@Test
			@DisplayName("viable moves")
			void viableMoves() {
				assertThat(goodHist.movesFrom(E1)).containsExactlyInAnyOrder(F1, G1);
			}

			@Test
			@DisplayName("correct new board")
			void correctNewBoard() {
				beSituation(goodHist.as(WHITE).playMove(E1, G1), """
PPPPPPPP
R  Q RK """);
			}
		}
	}


	@Nested
	@DisplayName("queen side")
	class Queenside {

		Board goodHist = visual.str2Obj("""
PPPPPPPP
R   KB R""");
		Board badHist = goodHist.updateHistory(h -> h.withoutCastles(WHITE));

		@Nested
		@DisplayName("impossible")
		class Impossible {

			@Test
			@DisplayName("queen in the way")
			void queenInTheWay() {
				assertThat(goodHist.placeAt(WHITE.queen(), D1).movesFrom(E1)).isEmpty();
			}
			@Test
			@DisplayName("bishop in the way")
			void bishopInTheWay() {
				assertThat(goodHist.placeAt(WHITE.bishop(), C1).movesFrom(E1)).containsExactly(D1);
			}
			@Test
			@DisplayName("knight in the way")
			void knightInTheWay() {
				assertThat(goodHist.placeAt(WHITE.knight(), B1).movesFrom(E1)).containsExactly(D1);
			}
			@Test
			@DisplayName("not allowed by history")
			void badHistory() {
				assertThat(badHist.movesFrom(E1)).containsExactly(D1);
			}
		}

		@Nested
		@DisplayName("possible")
		class Possible {
			@Test
			@DisplayName("viable moves")
			void viableMoves() {
				assertThat(goodHist.movesFrom(E1)).containsExactlyInAnyOrder(D1, C1);
			}

			@Test
			@DisplayName("correct new board")
			void correctNewBoard() {
				beSituation(goodHist.as(WHITE).playMove(E1, C1), """
PPPPPPPP
  KR B R""");
			}
		}
	}

	@Nested
	@DisplayName("impact history")
	class ImpactHistory {
		Board board = visual.str2Obj("""
PPPPPPPP
R   K  R""").withHistory(History.castle(WHITE, true, true));
		Situation situation = board.as(WHITE);

		@Nested
		@DisplayName("if king castles kingside")
		class CastlesKingside {
			Situation s2 = situation.playMove(E1, G1);

			@Test
			@DisplayName("correct new board")
			void correctNewBoard() {
				beSituation(s2, """
PPPPPPPP
R    RK """);
			}

			@Test
			@DisplayName("cannot castle queenside anymore")
			void cantCastleQueenside() {
				assertThat(s2.board.movesFrom(G1)).containsExactly(H1);
			}

			@Test
			@DisplayName("cannot castle kingside anymore even if the position looks good")
			void cantCastleKingside() {
				assertThat(s2.board.moveTo(F1, H1).moveTo(G1, E1).movesFrom(E1)).containsExactlyInAnyOrder(D1, F1);
			}
		}

		@Nested
		@DisplayName("if king castles queenside")
		class CastlesQueenside {
			Situation s2 = situation.playMove(E1, C1);

			@Test
			@DisplayName("correct new board")
			void correctNewBoard() {
				beSituation(s2, """
PPPPPPPP
  KR   R""");
			}

			@Test
			@DisplayName("cannot castle kingside anymore")
			void cantCastleKingside() {
				assertThat(s2.board.movesFrom(C1)).containsExactly(B1);
			}

			@Test
			@DisplayName("cannot castle queenside anymore even if the position looks good")
			void cantCastleQueenside() {
				assertThat(s2.board.moveTo(D1, A1).moveTo(C1, E1).movesFrom(E1)).containsExactlyInAnyOrder(D1, F1);
			}
		}

		@Nested
		@DisplayName("if kingside rook moves")
		class KingsideRookMoves {
			Situation s2 = situation.playMove(H1, G1).as(WHITE);

			@Test
			@DisplayName("can only castle queenside")
			void castleQueenside() {
				assertThat(s2.board.movesFrom(E1)).containsExactlyInAnyOrder(C1, D1, F1);
			}

			@Test
			@DisplayName("can't castle at all if queenside rook moves")
			void cantCastle() {
				assertThat(s2.playMove(A1, B1).board.movesFrom(E1)).containsExactlyInAnyOrder(D1, F1);
			}
		}

		@Nested
		@DisplayName("if queenside rook moves")
		class QueensideRookMoves {
			Situation s2 = situation.playMove(A1, B1).as(WHITE);

			@Test
			@DisplayName("can only castle kingside")
			void castleKingside() {
				assertThat(s2.board.movesFrom(E1)).containsExactlyInAnyOrder(D1, F1, G1);
			}

			@Test
			@DisplayName("can't castle at all if kingside rook moves")
			void cantCastle() {
				assertThat(s2.playMove(H1, G1).board.movesFrom(E1)).containsExactlyInAnyOrder(D1, F1);
			}
		}
	}
}

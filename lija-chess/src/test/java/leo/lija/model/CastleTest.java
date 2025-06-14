package leo.lija.model;

import leo.lija.format.VisualFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Pos.A1;
import static leo.lija.model.Pos.A2;
import static leo.lija.model.Pos.A3;
import static leo.lija.model.Pos.B1;
import static leo.lija.model.Pos.B2;
import static leo.lija.model.Pos.C1;
import static leo.lija.model.Pos.C2;
import static leo.lija.model.Pos.C3;
import static leo.lija.model.Pos.D1;
import static leo.lija.model.Pos.D2;
import static leo.lija.model.Pos.D3;
import static leo.lija.model.Pos.E1;
import static leo.lija.model.Pos.E2;
import static leo.lija.model.Pos.E3;
import static leo.lija.model.Pos.F1;
import static leo.lija.model.Pos.F2;
import static leo.lija.model.Pos.F3;
import static leo.lija.model.Pos.G1;
import static leo.lija.model.Pos.G3;
import static leo.lija.model.Pos.H1;
import static leo.lija.model.Pos.H3;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("a king should castle")
public class CastleTest {



	VisualFormat visual = new VisualFormat();
	Utils utils = new Utils();
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
				utils.beSituation(goodHist.as(WHITE).playMove(E1, G1), """
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
				utils.beSituation(goodHist.as(WHITE).playMove(E1, C1), """
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
				utils.beSituation(s2, """
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
				utils.beSituation(s2, """
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
		@DisplayName("if king moves")
		class KingMoves {
			@Nested
			@DisplayName("to the right")
			class ToRight {
				Situation s2 = situation.playMove(E1, F1).as(WHITE);
				@Test
				@DisplayName("cannot castle anymore")
				void cantCastle() {
					assertThat(s2.board.movesFrom(F1)).containsExactlyInAnyOrder(E1, G1);
				}
				@Test
				@DisplayName("neither if the king comes back")
				void comesBack() {
					Situation s3 = s2.playMove(F1, E1).as(WHITE);
					assertThat(s3.board.movesFrom(E1)).containsExactlyInAnyOrder(D1, F1);
				}
			}
			@Nested
			@DisplayName("to the left")
			class ToLeft {
				Situation s2 = situation.playMove(E1, D1).as(WHITE);
				@Test
				@DisplayName("cannot castle anymore")
				void cantCastle() {
					assertThat(s2.board.movesFrom(D1)).containsExactlyInAnyOrder(C1, E1);
				}
				@Test
				@DisplayName("neither if the king comes back")
				void comesBack() {
					Situation s3 = s2.playMove(D1, E1).as(WHITE);
					assertThat(s3.board.movesFrom(E1)).containsExactlyInAnyOrder(D1, F1);
				}
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

	@Nested
	@DisplayName("threat to king prevents castling")
	class ThreatToKing {
		Board board = visual.str2Obj("""
R   K  R""");

		@Test
		@DisplayName("by rook")
		void byRook() {
			assertThat(board.placeAt(BLACK.rook(), E3).movesFrom(E1)).containsExactlyInAnyOrder(D1, D2, F1, F2);
		}

		@Test
		@DisplayName("by knight")
		void byKnight() {
			assertThat(board.placeAt(BLACK.knight(), D3).movesFrom(E1)).containsExactlyInAnyOrder(D1, D2, E2, F1);
		}
	}

	@Nested
	@DisplayName("threat on castle trip prevents castling")
	class ThreatOnCastleTrip {

		@Nested
		@DisplayName("king side")
		class KingSide {
			Board board = visual.str2Obj("""
R  QK  R""");
			@Test
			void close() {
				assertThat(board.placeAt(BLACK.rook(), F3).movesFrom(E1)).containsExactlyInAnyOrder(D2, E2);
			}
			@Test
			void far() {
				assertThat(board.placeAt(BLACK.rook(), G3).movesFrom(E1)).containsExactlyInAnyOrder(D2, E2, F2, F1);
			}
		}
		@Nested
		@DisplayName("queen side")
		class QueenSide {
			Board board = visual.str2Obj("""
R   KB R""");
			@Test
			void close() {
				assertThat(board.placeAt(BLACK.rook(), D3).movesFrom(E1)).containsExactlyInAnyOrder(E2, F2);
			}
			@Test
			void far() {
				assertThat(board.placeAt(BLACK.rook(), C3).movesFrom(E1)).containsExactlyInAnyOrder(D1, D2, E2, F2);
			}
		}
		@Nested
		@DisplayName("chess 960")
		class Chess960 {
			@Nested
			@DisplayName("far kingside")
			class FarKingside {
				Board board = visual.str2Obj("""
BK     R""");
				@Test
				@DisplayName("rook threat")
				void rookThreat() {
					assertThat(board.placeAt(BLACK.rook(), F3).movesFrom(B1)).containsExactlyInAnyOrder(A2, B2, C2, C1);
				}
				@Test
				@DisplayName("enemy king threat")
				void enemyKingThreat() {
					assertThat(board.placeAt(BLACK.king(), E2).movesFrom(B1)).containsExactlyInAnyOrder(A2, B2, C2, C1);
				}
			}
		}
	}

	@Nested
	@DisplayName("threat to rook does not prevent castling")
	class ThreatToRook {
		@Test
		@DisplayName("king side")
		void kingSide() {
			Board board = visual.str2Obj("""
R  QK  R""");
			assertThat(board.placeAt(BLACK.rook(), H3).movesFrom(E1)).containsExactlyInAnyOrder(D2, E2, F1, F2, G1);
		}
		@Test
		@DisplayName("queen side")
		void queenSide() {
			Board board = visual.str2Obj("""
R   KB R""");
			assertThat(board.placeAt(BLACK.rook(), A3).movesFrom(E1)).containsExactlyInAnyOrder(C1, D1, D2, E2, F2);
		}
	}
}

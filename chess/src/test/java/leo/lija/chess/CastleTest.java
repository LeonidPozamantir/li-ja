package leo.lija.chess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.A1;
import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A3;
import static leo.lija.chess.Pos.B1;
import static leo.lija.chess.Pos.B2;
import static leo.lija.chess.Pos.C1;
import static leo.lija.chess.Pos.C2;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.D1;
import static leo.lija.chess.Pos.D2;
import static leo.lija.chess.Pos.D3;
import static leo.lija.chess.Pos.E1;
import static leo.lija.chess.Pos.E2;
import static leo.lija.chess.Pos.E3;
import static leo.lija.chess.Pos.F1;
import static leo.lija.chess.Pos.F2;
import static leo.lija.chess.Pos.F3;
import static leo.lija.chess.Pos.G1;
import static leo.lija.chess.Pos.G3;
import static leo.lija.chess.Pos.H1;
import static leo.lija.chess.Pos.H3;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("a king should castle")
public class CastleTest extends Base {


	
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
					assertThat(goodHist.placeAt(WHITE.bishop(), F1).destsFrom(E1).get()).isEmpty();
				}
				@Test
				@DisplayName("knight in the way")
				void knightInTheWay() {
					assertThat(goodHist.placeAt(WHITE.knight(), G1).destsFrom(E1).get()).containsExactly(F1);
				}
				@Test
				@DisplayName("not allowed by history")
				void badHistory() {
					assertThat(badHist.destsFrom(E1).get()).containsExactly(F1);
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
					assertThat(board960.placeAt(WHITE.bishop(), D1).destsFrom(C1).get()).isEmpty();
				}
				@Test
				@DisplayName("knight in the way")
				void knightInTheWay() {
					assertThat(board960.placeAt(WHITE.knight(), F1).destsFrom(C1).get()).containsExactly(D1);
				}
			}

		}

		@Nested
		@DisplayName("possible")
		class Possible {
			Game game = new Game(goodHist, WHITE);
			@Test
			@DisplayName("viable moves")
			void viableMoves() {
				assertThat(game.getBoard().destsFrom(E1).get()).containsExactlyInAnyOrder(F1, G1);
			}

			@Test
			@DisplayName("correct new board")
			void correctNewBoard() {
				beGame(game.playMove(E1, G1), """
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
				assertThat(goodHist.placeAt(WHITE.queen(), D1).destsFrom(E1).get()).isEmpty();
			}
			@Test
			@DisplayName("bishop in the way")
			void bishopInTheWay() {
				assertThat(goodHist.placeAt(WHITE.bishop(), C1).destsFrom(E1).get()).containsExactly(D1);
			}
			@Test
			@DisplayName("knight in the way")
			void knightInTheWay() {
				assertThat(goodHist.placeAt(WHITE.knight(), B1).destsFrom(E1).get()).containsExactly(D1);
			}
			@Test
			@DisplayName("not allowed by history")
			void badHistory() {
				assertThat(badHist.destsFrom(E1).get()).containsExactly(D1);
			}
		}

		@Nested
		@DisplayName("possible")
		class Possible {
			Game game = new Game(goodHist, WHITE);
			@Test
			@DisplayName("viable moves")
			void viableMoves() {
				assertThat(game.getBoard().destsFrom(E1).get()).containsExactlyInAnyOrder(D1, C1);
			}

			@Test
			@DisplayName("correct new board")
			void correctNewBoard() {
				beGame(game.playMove(E1, C1), """
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
		RichGame game = new RichGame(board, WHITE);

		@Nested
		@DisplayName("if king castles kingside")
		class CastlesKingside {
			Game g2 = game.playMove(E1, G1);

			@Test
			@DisplayName("correct new board")
			void correctNewBoard() {
				beGame(g2, """
PPPPPPPP
R    RK """);
			}

			@Test
			@DisplayName("cannot castle queenside anymore")
			void cantCastleQueenside() {
				assertThat(g2.getBoard().destsFrom(G1).get()).containsExactly(H1);
			}

			@Test
			@DisplayName("cannot castle kingside anymore even if the position looks good")
			void cantCastleKingside() {
				assertThat(g2.getBoard().moveTo(F1, H1).moveTo(G1, E1).destsFrom(E1).get()).containsExactlyInAnyOrder(D1, F1);
			}
		}

		@Nested
		@DisplayName("if king castles queenside")
		class CastlesQueenside {
			Game g2 = game.playMove(E1, C1);

			@Test
			@DisplayName("correct new board")
			void correctNewBoard() {
				beGame(g2, """
PPPPPPPP
  KR   R""");
			}

			@Test
			@DisplayName("cannot castle kingside anymore")
			void cantCastleKingside() {
				assertThat(g2.getBoard().destsFrom(C1).get()).containsExactly(B1);
			}

			@Test
			@DisplayName("cannot castle queenside anymore even if the position looks good")
			void cantCastleQueenside() {
				assertThat(g2.getBoard().moveTo(D1, A1).moveTo(C1, E1).destsFrom(E1).get()).containsExactlyInAnyOrder(D1, F1);
			}
		}

		@Nested
		@DisplayName("if king moves")
		class KingMoves {
			@Nested
			@DisplayName("to the right")
			class ToRight {
				RichGame g2 = game.playMove(E1, F1).as(WHITE);
				@Test
				@DisplayName("cannot castle anymore")
				void cantCastle() {
					assertThat(g2.getBoard().destsFrom(F1).get()).containsExactlyInAnyOrder(E1, G1);
				}
				@Test
				@DisplayName("neither if the king comes back")
				void comesBack() {
					RichGame g3 = g2.playMove(F1, E1).as(WHITE);
					assertThat(g3.getBoard().destsFrom(E1).get()).containsExactlyInAnyOrder(D1, F1);
				}
			}
			@Nested
			@DisplayName("to the left")
			class ToLeft {
				RichGame g2 = game.playMove(E1, D1).as(WHITE);
				@Test
				@DisplayName("cannot castle anymore")
				void cantCastle() {
					assertThat(g2.getBoard().destsFrom(D1).get()).containsExactlyInAnyOrder(C1, E1);
				}
				@Test
				@DisplayName("neither if the king comes back")
				void comesBack() {
					RichGame g3 = g2.playMove(D1, E1).as(WHITE);
					assertThat(g3.getBoard().destsFrom(E1).get()).containsExactlyInAnyOrder(D1, F1);
				}
			}
		}

		@Nested
		@DisplayName("if kingside rook moves")
		class KingsideRookMoves {
			Game g2 = game.playMove(H1, G1).as(WHITE);

			@Test
			@DisplayName("can only castle queenside")
			void castleQueenside() {
				assertThat(g2.getBoard().destsFrom(E1).get()).containsExactlyInAnyOrder(C1, D1, F1);
			}

			@Test
			@DisplayName("can't castle at all if queenside rook moves")
			void cantCastle() {
				assertThat(g2.playMove(A1, B1).getBoard().destsFrom(E1).get()).containsExactlyInAnyOrder(D1, F1);
			}
		}

		@Nested
		@DisplayName("if queenside rook moves")
		class QueensideRookMoves {
			RichGame g2 = game.playMove(A1, B1).as(WHITE);

			@Test
			@DisplayName("can only castle kingside")
			void castleKingside() {
				assertThat(g2.getBoard().destsFrom(E1).get()).containsExactlyInAnyOrder(D1, F1, G1);
			}

			@Test
			@DisplayName("can't castle at all if kingside rook moves")
			void cantCastle() {
				assertThat(g2.playMove(H1, G1).getBoard().destsFrom(E1).get()).containsExactlyInAnyOrder(D1, F1);
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
			assertThat(board.placeAt(BLACK.rook(), E3).destsFrom(E1).get()).containsExactlyInAnyOrder(D1, D2, F1, F2);
		}

		@Test
		@DisplayName("by knight")
		void byKnight() {
			assertThat(board.placeAt(BLACK.knight(), D3).destsFrom(E1).get()).containsExactlyInAnyOrder(D1, D2, E2, F1);
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
				assertThat(board.placeAt(BLACK.rook(), F3).destsFrom(E1).get()).containsExactlyInAnyOrder(D2, E2);
			}
			@Test
			void far() {
				assertThat(board.placeAt(BLACK.rook(), G3).destsFrom(E1).get()).containsExactlyInAnyOrder(D2, E2, F2, F1);
			}
		}
		@Nested
		@DisplayName("queen side")
		class QueenSide {
			Board board = visual.str2Obj("""
R   KB R""");
			@Test
			void close() {
				assertThat(board.placeAt(BLACK.rook(), D3).destsFrom(E1).get()).containsExactlyInAnyOrder(E2, F2);
			}
			@Test
			void far() {
				assertThat(board.placeAt(BLACK.rook(), C3).destsFrom(E1).get()).containsExactlyInAnyOrder(D1, D2, E2, F2);
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
					assertThat(board.placeAt(BLACK.rook(), F3).destsFrom(B1).get()).containsExactlyInAnyOrder(A2, B2, C2, C1);
				}
				@Test
				@DisplayName("enemy king threat")
				void enemyKingThreat() {
					assertThat(board.placeAt(BLACK.king(), E2).destsFrom(B1).get()).containsExactlyInAnyOrder(A2, B2, C2, C1);
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
			assertThat(board.placeAt(BLACK.rook(), H3).destsFrom(E1).get()).containsExactlyInAnyOrder(D2, E2, F1, F2, G1);
		}
		@Test
		@DisplayName("queen side")
		void queenSide() {
			Board board = visual.str2Obj("""
R   KB R""");
			assertThat(board.placeAt(BLACK.rook(), A3).destsFrom(E1).get()).containsExactlyInAnyOrder(C1, D1, D2, E2, F2);
		}
	}
}

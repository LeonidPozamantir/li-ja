package leo.lija.model;

import leo.lija.format.VisualFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Pos.B1;
import static leo.lija.model.Pos.C1;
import static leo.lija.model.Pos.D1;
import static leo.lija.model.Pos.E1;
import static leo.lija.model.Pos.F1;
import static leo.lija.model.Pos.G1;
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

		Board badHist = visual.str2Obj("""
PPPPPPPP
R  QK  R""").withHistory(History.castle(WHITE, false, false));
		Board goodHist = badHist.withHistory(History.castle(WHITE, true, true));

		@Nested
		@DisplayName("impossible")
		class Impossible {

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

		Board badHist = visual.str2Obj("""
PPPPPPPP
R   KB R""").withHistory(History.castle(WHITE, false, false));
		Board goodHist = badHist.withHistory(History.castle(WHITE, true, true));

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
				assertThat(goodHist.movesFrom(E1)).containsExactlyInAnyOrder(D1, B1);
			}

			@Test
			@DisplayName("correct new board")
			void correctNewBoard() {
				beSituation(goodHist.as(WHITE).playMove(E1, B1), """
PPPPPPPP
  KR B R""");
			}
		}
	}
}

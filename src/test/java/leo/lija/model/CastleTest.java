package leo.lija.model;

import leo.lija.format.Visual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.model.Pos.C1;
import static leo.lija.model.Pos.D1;
import static leo.lija.model.Pos.E1;
import static leo.lija.model.Pos.F1;
import static leo.lija.model.Pos.G1;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("a king should castle")
public class CastleTest {

	Visual visual = new Visual();
	@Nested
	@DisplayName("king side")
	class Kingside {

		@Nested
		@DisplayName("impossible")
		class Impossible {

			@Test
			@DisplayName("pieces in the way")
			void piecesInTheWay() {
				assertThat(new Board().movesFrom(E1)).isEmpty();
			}
		}

		@Nested
		@DisplayName("possible")
		class Possible {

			Board board = visual.str2Obj("""
PPPPPPPP
R  QK  R""");
			@Test
			@DisplayName("viable moves")
			void viableMoves() {
				assertThat(board.movesFrom(E1)).containsExactlyInAnyOrder(F1, G1);
			}
		}
	}


	@Nested
	@DisplayName("queen side")
	class Queenside {

		@Nested
		@DisplayName("impossible")
		class Impossible {

			@Test
			@DisplayName("pieces in the way")
			void piecesInTheWay() {
				assertThat(new Board().movesFrom(E1)).isEmpty();
			}
		}

		@Nested
		@DisplayName("possible")
		class Possible {

			Board board = visual.str2Obj("""
PPPPPP
R   KB""");
			@Test
			@DisplayName("viable moves")
			void viableMoves() {
				assertThat(board.movesFrom(E1)).containsExactlyInAnyOrder(D1, C1);
			}
		}
	}
}

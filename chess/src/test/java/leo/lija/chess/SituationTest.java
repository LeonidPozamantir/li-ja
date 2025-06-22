package leo.lija.chess;

import leo.lija.chess.format.VisualFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("a game should")
public class SituationTest {

	@Nested
	@DisplayName("detect")
	class Detect {

		VisualFormat visual = new VisualFormat();

		@Nested
		@DisplayName("check")
		class Check {

			@Test
			@DisplayName("by rook")
			void byRook() {
				assertThat(visual.str2Obj("""
K  r
""").as(WHITE).check()).isTrue();
			}

			@Test
			@DisplayName("by knight")
			void byKnight() {
				assertThat(visual.str2Obj("""
  n
K
""").as(WHITE).check()).isTrue();
			}

			@Test
			@DisplayName("not")
			void not() {
				assertThat(visual.str2Obj("""
   n
K
""").as(WHITE).check()).isFalse();
			}
		}

		@Nested
		@DisplayName("checkmate")
		class Checkmate {

			@Test
			@DisplayName("by rook")
			void byRook() {
				assertThat(visual.str2Obj("""
PP
K  r
""").as(WHITE).checkmate()).isTrue();
			}

			@Test
			@DisplayName("by knight")
			void byKnight() {
				assertThat(visual.str2Obj("""
PPn
KR
""").as(WHITE).checkmate()).isTrue();
			}

			@Test
			@DisplayName("not")
			void not() {
				assertThat(visual.str2Obj("""
   n
K
""").as(WHITE).checkmate()).isFalse();
			}
		}

		@Nested
		@DisplayName("stalemate")
		class Stalemate {

			@Test
			@DisplayName("stuck in a corner")
			void stuckInACorner() {
				assertThat(visual.str2Obj("""
prr
K
""").as(WHITE).stalemate()).isTrue();
			}

			@Test
			@DisplayName("not")
			void not() {
				assertThat(visual.str2Obj("""
  b
K
""").as(WHITE).stalemate()).isFalse();
			}
		}
	}
}

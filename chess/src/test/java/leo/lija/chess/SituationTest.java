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
				assertThat(visual.str2Sit("""
K  r
""", WHITE).check()).isTrue();
			}

			@Test
			@DisplayName("by knight")
			void byKnight() {
				assertThat(visual.str2Sit("""
  n
K
""", WHITE).check()).isTrue();
			}

			@Test
			@DisplayName("not")
			void not() {
				assertThat(visual.str2Sit("""
   n
K
""", WHITE).check()).isFalse();
			}
		}

		@Nested
		@DisplayName("checkmate")
		class Checkmate {

			@Test
			@DisplayName("by rook")
			void byRook() {
				assertThat(visual.str2Sit("""
PP
K  r
""", WHITE).checkmate()).isTrue();
			}

			@Test
			@DisplayName("by knight")
			void byKnight() {
				assertThat(visual.str2Sit("""
PPn
KR
""", WHITE).checkmate()).isTrue();
			}

			@Test
			@DisplayName("not")
			void not() {
				assertThat(visual.str2Sit("""
   n
K
""", WHITE).checkmate()).isFalse();
			}
		}

		@Nested
		@DisplayName("stalemate")
		class Stalemate {

			@Test
			@DisplayName("stuck in a corner")
			void stuckInACorner() {
				assertThat(visual.str2Sit("""
prr
K
""", WHITE).stalemate()).isTrue();
			}

			@Test
			@DisplayName("not")
			void not() {
				assertThat(visual.str2Sit("""
  b
K
""", WHITE).stalemate()).isFalse();
			}
		}
	}
}

package leo.lija.model;

import leo.lija.format.Visual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.model.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("a game should")
public class SituationTest {

	@Nested
	@DisplayName("detect")
	class Detect {

		Visual visual = new Visual();

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
		}
	}
}

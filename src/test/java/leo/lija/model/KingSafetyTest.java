package leo.lija.model;

import leo.lija.format.Visual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.model.Pos.E1;
import static leo.lija.model.Pos.F1;
import static leo.lija.model.Pos.F2;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("King should")
class KingSafetyTest {

	Visual visual = new Visual();

	@Test
	@DisplayName("not commit suicide")
	void notCommitSuicide() {
		Board board = visual.str2Obj("""
	P n
PPPP   P
RNBQK  R""");
		assertThat(board.movesFrom(E1)).containsExactly(F2);
	}

	@Test
	@DisplayName("escape from danger")
	void escapeFromDanger() {
		Board board = visual.str2Obj("""
    r

PPPP   P
RNBQK  R""");
		assertThat(board.movesFrom(E1)).containsExactlyInAnyOrder(F1, F2);
	}
}

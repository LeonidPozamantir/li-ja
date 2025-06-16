package leo.lija;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.Color.BLACK;
import static leo.lija.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;

class PieceTest {

	@Test
	@DisplayName("Piece equality")
	void pieceEquality() {
		assertThat(WHITE.getOpposite().pawn()).isEqualTo(BLACK.pawn());
	}
}

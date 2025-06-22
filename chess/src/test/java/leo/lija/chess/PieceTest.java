package leo.lija.chess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;

class PieceTest {

	@Test
	@DisplayName("Piece equality")
	void pieceEquality() {
		assertThat(WHITE.getOpposite().pawn()).isEqualTo(BLACK.pawn());
	}
}

package leo.lija.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static leo.lija.model.Pos.A4;
import static leo.lija.model.Pos.A8;
import static leo.lija.model.Pos.B4;
import static leo.lija.model.Pos.B8;
import static leo.lija.model.Pos.C4;
import static leo.lija.model.Pos.C8;
import static leo.lija.model.Pos.D4;
import static leo.lija.model.Pos.D8;
import static leo.lija.model.Pos.E1;
import static leo.lija.model.Pos.E2;
import static leo.lija.model.Pos.E3;
import static leo.lija.model.Pos.E4;
import static leo.lija.model.Pos.E5;
import static leo.lija.model.Pos.E6;
import static leo.lija.model.Pos.E7;
import static leo.lija.model.Pos.E8;
import static leo.lija.model.Pos.F4;
import static leo.lija.model.Pos.F8;
import static leo.lija.model.Pos.G4;
import static leo.lija.model.Pos.G8;
import static leo.lija.model.Pos.H1;
import static leo.lija.model.Pos.H2;
import static leo.lija.model.Pos.H3;
import static leo.lija.model.Pos.H4;
import static leo.lija.model.Pos.H5;
import static leo.lija.model.Pos.H6;
import static leo.lija.model.Pos.H7;
import static leo.lija.model.Pos.H8;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Rook should")
public class RookTest {

    private final Piece rook = new Piece(Color.WHITE, Role.ROOK);
    private final Board board = Board.empty();
    private Board boardWithRookAt(Pos pos) {
        return board.placeAt(rook, pos);
    }
    private Set<Pos> basicMoves(Pos pos) {
        return rook.basicMoves(pos, board);
    }

    @Test
    @DisplayName("be able to move to any position along the same rank or file")
    void testBasicMoves() {
        assertThat(basicMoves(E4)).containsExactly(E5, E6, E7, E8, E3, E2, E1, F4, G4, H4, D4, C4, B4, A4);
    }

    @Test
    @DisplayName("be able to move to any position along the same rank or file when in edge")
    void testBasicMovesEdge() {
        assertThat(basicMoves(H8)).containsExactly(H7, H6, H5, H4, H3, H2, H1, G8, F8, E8, D8, C8, B8, A8);
    }
}

package leo.lija;

import leo.lija.model.Board;
import leo.lija.model.Color;
import leo.lija.model.Piece;
import leo.lija.model.Pos;
import leo.lija.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Role.ROOK;
import static org.assertj.core.api.Assertions.assertThat;

public class BoardTest {

    private Board board = new Board();

    @Test
    @DisplayName("Should have pieces by default")
    public void hasPieces() {
        assertThat(board.getPieces()).isNotEmpty();
    }

    @Test
    @DisplayName("Nothing should be taken by default")
    public void nothingTaken() {
        assertThat(board.getTaken()).isEmpty();
    }

    @Test
    @DisplayName("Should allow piece to be places")
    public void pieceCanBePlaced() {
        Board newBoard = board.placeAt(new Piece(WHITE, ROOK), Pos.fromString("b3"));
        assertThat(newBoard.at(Pos.fromString("b3"))).contains(new Piece(WHITE, ROOK));
    }

}

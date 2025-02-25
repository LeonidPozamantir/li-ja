package leo.lija;

import leo.lija.model.Board;
import leo.lija.model.Piece;
import leo.lija.model.Pos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Role.ROOK;
import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {

    private final Board board = new Board();

    @Test
    @DisplayName("Should have pieces by default")
    void hasPieces() {
        assertThat(board.getPieces()).isNotEmpty();
    }

    @Test
    @DisplayName("Should allow piece to be placed")
    void pieceCanBePlaced() {
        Board newBoard = board.placeAt(new Piece(WHITE, ROOK), Pos.at("e3"));
        assertThat(newBoard.at(Pos.at("e3"))).contains(new Piece(WHITE, ROOK));
    }

    @Test
    @DisplayName("Should allow piece to be taken")
    void pieceCanBeTaken() {
        Board newBoard = board.take(Pos.at("a1"));
        assertThat(newBoard.at(Pos.at("a1"))).isEmpty();
    }

}

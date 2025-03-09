package leo.lija.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Pos.*;
import static leo.lija.model.Role.BISHOP;
import static leo.lija.model.Role.KING;
import static leo.lija.model.Role.KNIGHT;
import static leo.lija.model.Role.PAWN;
import static leo.lija.model.Role.QUEEN;
import static leo.lija.model.Role.ROOK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Board newBoard = board.placeAt(new Piece(WHITE, ROOK), E3);
        assertThat(newBoard.at(E3)).contains(new Piece(WHITE, ROOK));
    }

    @Test
    @DisplayName("Should allow piece to be taken")
    void pieceCanBeTaken() {
        Board newBoard = board.take(A1);
        assertThat(newBoard.at(A1)).isEmpty();
    }

    @Test
    @DisplayName("Should position white pieces correctly")
    void whitePiecesPositionedCorrectly() {
        assertThat(board.at(A1)).contains(new Piece(WHITE, ROOK));
        assertThat(board.at(B1)).contains(new Piece(WHITE, KNIGHT));
        assertThat(board.at(C1)).contains(new Piece(WHITE, BISHOP));
        assertThat(board.at(D1)).contains(new Piece(WHITE, QUEEN));
        assertThat(board.at(E1)).contains(new Piece(WHITE, KING));
        assertThat(board.at(F1)).contains(new Piece(WHITE, BISHOP));
        assertThat(board.at(G1)).contains(new Piece(WHITE, KNIGHT));
        assertThat(board.at(H1)).contains(new Piece(WHITE, ROOK));
        assertThat(board.at(A2)).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(B2)).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(C2)).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(D2)).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(E2)).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(F2)).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(G2)).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(H2)).contains(new Piece(WHITE, PAWN));
    }

    @Test
    @DisplayName("Should position black pieces correctly")
    void blackPiecesPositionedCorrectly() {
        assertThat(board.at(A7)).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(B7)).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(C7)).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(D7)).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(E7)).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(F7)).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(G7)).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(H7)).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(A8)).contains(new Piece(BLACK, ROOK));
        assertThat(board.at(B8)).contains(new Piece(BLACK, KNIGHT));
        assertThat(board.at(C8)).contains(new Piece(BLACK, BISHOP));
        assertThat(board.at(D8)).contains(new Piece(BLACK, QUEEN));
        assertThat(board.at(E8)).contains(new Piece(BLACK, KING));
        assertThat(board.at(F8)).contains(new Piece(BLACK, BISHOP));
        assertThat(board.at(G8)).contains(new Piece(BLACK, KNIGHT));
        assertThat(board.at(H8)).contains(new Piece(BLACK, ROOK));
    }

    @Test
    @DisplayName("Should allow piece to be move")
    void pieceCanBeMoved() {
        Board newBoard = board.moveTo(E2, E4);
        assertThat(newBoard.at(E2)).isEmpty();
        assertThat(newBoard.at(E4)).contains(new Piece(WHITE, PAWN));
    }

    @Test
    @DisplayName("Should not allow empty place to move")
    void emptyPlaceCannotBeMoved() {
        assertThrows(Exception.class, () -> board.moveTo(E5, E6));
    }

    @Test
    @DisplayName("Should not allow to move to occupied position")
    void canNotBeMoveToOccupiedPosition() {
        assertThrows(Exception.class, () -> board.moveTo(A1, A2));
    }
}

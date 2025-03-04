package leo.lija.model;

import leo.lija.exceptions.ChessRulesException;
import leo.lija.model.Board;
import leo.lija.model.Piece;
import leo.lija.model.Pos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;
import static leo.lija.model.Role.BISHOP;
import static leo.lija.model.Role.KING;
import static leo.lija.model.Role.KNIGHT;
import static leo.lija.model.Role.PAWN;
import static leo.lija.model.Role.QUEEN;
import static leo.lija.model.Role.ROOK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        Board newBoard = board.placeAt(new Piece(WHITE, ROOK), Pos.at("e3"));
        assertThat(newBoard.at(Pos.at("e3"))).contains(new Piece(WHITE, ROOK));
    }

    @Test
    @DisplayName("Should allow piece to be taken")
    void pieceCanBeTaken() {
        Board newBoard = board.take(Pos.at("a1"));
        assertThat(newBoard.at(Pos.at("a1"))).isEmpty();
    }

    @Test
    @DisplayName("Should position pieces correctly")
    void piecesPositionedCorrectly() {
        assertThat(board.at(Pos.at("a1"))).contains(new Piece(WHITE, ROOK));
        assertThat(board.at(Pos.at("b1"))).contains(new Piece(WHITE, KNIGHT));
        assertThat(board.at(Pos.at("c1"))).contains(new Piece(WHITE, BISHOP));
        assertThat(board.at(Pos.at("d1"))).contains(new Piece(WHITE, QUEEN));
        assertThat(board.at(Pos.at("e1"))).contains(new Piece(WHITE, KING));
        assertThat(board.at(Pos.at("f1"))).contains(new Piece(WHITE, BISHOP));
        assertThat(board.at(Pos.at("g1"))).contains(new Piece(WHITE, KNIGHT));
        assertThat(board.at(Pos.at("h1"))).contains(new Piece(WHITE, ROOK));
        assertThat(board.at(Pos.at("a2"))).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(Pos.at("b2"))).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(Pos.at("c2"))).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(Pos.at("d2"))).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(Pos.at("e2"))).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(Pos.at("f2"))).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(Pos.at("g2"))).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(Pos.at("h2"))).contains(new Piece(WHITE, PAWN));
        assertThat(board.at(Pos.at("a7"))).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(Pos.at("b7"))).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(Pos.at("c7"))).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(Pos.at("d7"))).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(Pos.at("e7"))).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(Pos.at("f7"))).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(Pos.at("g7"))).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(Pos.at("h7"))).contains(new Piece(BLACK, PAWN));
        assertThat(board.at(Pos.at("a8"))).contains(new Piece(BLACK, ROOK));
        assertThat(board.at(Pos.at("b8"))).contains(new Piece(BLACK, KNIGHT));
        assertThat(board.at(Pos.at("c8"))).contains(new Piece(BLACK, BISHOP));
        assertThat(board.at(Pos.at("d8"))).contains(new Piece(BLACK, QUEEN));
        assertThat(board.at(Pos.at("e8"))).contains(new Piece(BLACK, KING));
        assertThat(board.at(Pos.at("f8"))).contains(new Piece(BLACK, BISHOP));
        assertThat(board.at(Pos.at("g8"))).contains(new Piece(BLACK, KNIGHT));
        assertThat(board.at(Pos.at("h8"))).contains(new Piece(BLACK, ROOK));
    }

    @Test
    @DisplayName("Should allow piece to be move")
    void pieceCanBeMoved() {
        Board newBoard = board.moveTo(Pos.at("e2"), Pos.at("e4"));
        assertThat(newBoard.at(Pos.at("e2"))).isEmpty();
        assertThat(newBoard.at(Pos.at("e4"))).contains(new Piece(WHITE, PAWN));
    }

    @Test
    @DisplayName("Should not allow empty place to move")
    void emptyPlaceCannotBeMoved() {
        Pos from = Pos.at("e5");
        Pos to = Pos.at("e6");
        assertThrows(Exception.class, () -> board.moveTo(from, to));
    }

    @Test
    @DisplayName("Should not allow to move to occupied position")
    void canNotBeMoveToOccupiedPosition() {
        Pos from = Pos.at("a1");
        Pos to = Pos.at("a2");
        assertThrows(Exception.class, () -> board.moveTo(from, to));
    }
}

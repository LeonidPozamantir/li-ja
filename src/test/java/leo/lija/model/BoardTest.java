package leo.lija.model;

import leo.lija.exceptions.ChessRulesException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    private final Board newGameBoard = new Board();

    @Test
    @DisplayName("Should have pieces by default")
    void hasPieces() {
        assertThat(newGameBoard.getPieces()).isNotEmpty();
    }

    @Test
    @DisplayName("Should allow piece to be placed")
    void pieceCanBePlaced() {
        Board newBoard = newGameBoard.placeAt(new Piece(WHITE, ROOK), E3);
        assertThat(newBoard.at(E3)).contains(new Piece(WHITE, ROOK));
    }

    @Test
    @DisplayName("Should allow piece to be taken")
    void pieceCanBeTaken() {
        Board newBoard = newGameBoard.take(A1);
        assertThat(newBoard.at(A1)).isEmpty();
    }

    @Test
    @DisplayName("Should position white pieces correctly")
    void whitePiecesPositionedCorrectly() {
        assertThat(newGameBoard.at(A1)).contains(new Piece(WHITE, ROOK));
        assertThat(newGameBoard.at(B1)).contains(new Piece(WHITE, KNIGHT));
        assertThat(newGameBoard.at(C1)).contains(new Piece(WHITE, BISHOP));
        assertThat(newGameBoard.at(D1)).contains(new Piece(WHITE, QUEEN));
        assertThat(newGameBoard.at(E1)).contains(new Piece(WHITE, KING));
        assertThat(newGameBoard.at(F1)).contains(new Piece(WHITE, BISHOP));
        assertThat(newGameBoard.at(G1)).contains(new Piece(WHITE, KNIGHT));
        assertThat(newGameBoard.at(H1)).contains(new Piece(WHITE, ROOK));
        assertThat(newGameBoard.at(A2)).contains(new Piece(WHITE, PAWN));
        assertThat(newGameBoard.at(B2)).contains(new Piece(WHITE, PAWN));
        assertThat(newGameBoard.at(C2)).contains(new Piece(WHITE, PAWN));
        assertThat(newGameBoard.at(D2)).contains(new Piece(WHITE, PAWN));
        assertThat(newGameBoard.at(E2)).contains(new Piece(WHITE, PAWN));
        assertThat(newGameBoard.at(F2)).contains(new Piece(WHITE, PAWN));
        assertThat(newGameBoard.at(G2)).contains(new Piece(WHITE, PAWN));
        assertThat(newGameBoard.at(H2)).contains(new Piece(WHITE, PAWN));
    }

    @Test
    @DisplayName("Should position black pieces correctly")
    void blackPiecesPositionedCorrectly() {
        assertThat(newGameBoard.at(A7)).contains(new Piece(BLACK, PAWN));
        assertThat(newGameBoard.at(B7)).contains(new Piece(BLACK, PAWN));
        assertThat(newGameBoard.at(C7)).contains(new Piece(BLACK, PAWN));
        assertThat(newGameBoard.at(D7)).contains(new Piece(BLACK, PAWN));
        assertThat(newGameBoard.at(E7)).contains(new Piece(BLACK, PAWN));
        assertThat(newGameBoard.at(F7)).contains(new Piece(BLACK, PAWN));
        assertThat(newGameBoard.at(G7)).contains(new Piece(BLACK, PAWN));
        assertThat(newGameBoard.at(H7)).contains(new Piece(BLACK, PAWN));
        assertThat(newGameBoard.at(A8)).contains(new Piece(BLACK, ROOK));
        assertThat(newGameBoard.at(B8)).contains(new Piece(BLACK, KNIGHT));
        assertThat(newGameBoard.at(C8)).contains(new Piece(BLACK, BISHOP));
        assertThat(newGameBoard.at(D8)).contains(new Piece(BLACK, QUEEN));
        assertThat(newGameBoard.at(E8)).contains(new Piece(BLACK, KING));
        assertThat(newGameBoard.at(F8)).contains(new Piece(BLACK, BISHOP));
        assertThat(newGameBoard.at(G8)).contains(new Piece(BLACK, KNIGHT));
        assertThat(newGameBoard.at(H8)).contains(new Piece(BLACK, ROOK));
    }

    @Test
    @DisplayName("Should allow piece to be move")
    void pieceCanBeMoved() {
        Board newBoard = newGameBoard.moveTo(E2, E4);
        assertThat(newBoard.at(E2)).isEmpty();
        assertThat(newBoard.at(E4)).contains(new Piece(WHITE, PAWN));
    }

    @Test
    @DisplayName("Should not allow empty place to move")
    void emptyPlaceCannotBeMoved() {
        assertThrows(Exception.class, () -> newGameBoard.moveTo(E5, E6));
    }

    @Test
    @DisplayName("Should not allow to move to occupied position")
    void canNotBeMoveToOccupiedPosition() {
        assertThrows(Exception.class, () -> newGameBoard.moveTo(A1, A2));
    }

    @Test
    @DisplayName("should allow a pawn to be promoted to any role")
    void promoteToQRBN() {
        assertThat(List.of(QUEEN, ROOK, BISHOP, KNIGHT)).allMatch(r -> {
            Optional<Piece> op = Board.empty().placeAt(new Piece(WHITE, PAWN), A8).promoteTo(A8, r).at(A8);
            return op.isPresent() && op.get().role() == r;
        });
    }

    @Test
    @DisplayName("should not allow a pawn to be promoted to king or pawn")
    void promoteToPK() {
        assertThat(List.of(PAWN, KING)).allSatisfy(r -> {
            Board board = Board.empty().placeAt(new Piece(WHITE, PAWN), A8);
            assertThrows(ChessRulesException.class, () -> board.promoteTo(A8, r));
        });
    }

    @Test
    @DisplayName("should not allow an empty position to be promoted")
    void promoteEmpty() {
        assertThrows(ChessRulesException.class, () -> newGameBoard.promoteTo(A6, QUEEN));
    }

    @Test
    @DisplayName("should not allow to promote non-pawn")
    void promoteNonPawn() {
        assertThrows(ChessRulesException.class, () -> newGameBoard.promoteTo(A1, QUEEN));
    }

    @Test
    @DisplayName("should provide occupation map")
    void occupationMap() {
        Board board = new Board(Map.of(
                A2, new Piece(WHITE, PAWN),
                A3, new Piece(WHITE, PAWN),
                D1, new Piece(WHITE, KING),
                E8, new Piece(BLACK, KING),
                H4, new Piece(BLACK, QUEEN)
        ));

        assertThat(board.occupation()).contains(
                Map.entry(WHITE, Set.of(A2, A3, D1)),
                Map.entry(BLACK, Set.of(E8, H4))
        );
    }
}

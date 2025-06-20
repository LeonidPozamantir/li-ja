package leo.lija;

import leo.lija.format.VisualFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static leo.lija.Color.BLACK;
import static leo.lija.Color.WHITE;
import static leo.lija.Pos.*;
import static leo.lija.Role.BISHOP;
import static leo.lija.Role.KING;
import static leo.lija.Role.KNIGHT;
import static leo.lija.Role.PAWN;
import static leo.lija.Role.QUEEN;
import static leo.lija.Role.ROOK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardTest {

    private final Board newGameBoard = new Board();
    VisualFormat visual = new VisualFormat();

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
        Board newBoard = newGameBoard.takeValid(A1);
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
    @DisplayName("should allow a pawn to be promoted to a queen")
    void promoteToQueen() {
        Board b = Board.empty().placeAt(new Piece(WHITE, PAWN), A7).promote(A7, A8).get();
        assertThat(b.at(A8)).contains(WHITE.queen());
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

    @Nested
    @DisplayName("navigate in pos based on pieces")
    class Navigate {
        @Test
        @DisplayName("right to end")
        void rightToEnd() {
            Board board = visual.str2Obj("""
R   K  R""");
            assertThat(E1.multShiftRight(p -> board.occupations().contains(p))).containsExactlyInAnyOrder(F1, G1, H1);
        }

        @Test
        @DisplayName("right to next")
        void rightToNext() {
            Board board = visual.str2Obj("""
R   KB R""");
            assertThat(E1.multShiftRight(p -> board.occupations().contains(p))).containsExactly(F1);
        }

        @Test
        @DisplayName("left to end")
        void leftToEnd() {
            Board board = visual.str2Obj("""
R   K  R""");
            assertThat(E1.multShiftLeft(p -> board.occupations().contains(p))).containsExactlyInAnyOrder(D1, C1, B1, A1);
        }

        @Test
        @DisplayName("left to next")
        void leftToNext() {
            Board board = visual.str2Obj("""
R  BK  R""");
            assertThat(E1.multShiftLeft(p -> board.occupations().contains(p))).containsExactly(D1);
        }
    }
}

package leo.lija.chess;

import leo.lija.chess.format.VisualFormat;
import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.*;
import static leo.lija.chess.Role.BISHOP;
import static leo.lija.chess.Role.KING;
import static leo.lija.chess.Role.KNIGHT;
import static leo.lija.chess.Role.PAWN;
import static leo.lija.chess.Role.QUEEN;
import static leo.lija.chess.Role.ROOK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("should")
class BoardTest {

    private final Board newGameBoard = new Board();
    VisualFormat visual = new VisualFormat();

    @Test
    @DisplayName("have pieces by default")
    void hasPieces() {
        assertThat(newGameBoard.getPieces()).isNotEmpty();
    }

    @Test
    @DisplayName("allow piece to be placed")
    void pieceCanBePlaced() {
        Board newBoard = newGameBoard.placeAt(new Piece(WHITE, ROOK), E3);
        assertThat(newBoard.at(E3)).contains(new Piece(WHITE, ROOK));
    }

    @Test
    @DisplayName("allow piece to be taken")
    void pieceCanBeTaken() {
        Optional<Board> newBoard = newGameBoard.take(A1);
        assertThat(newBoard.get().at(A1)).isEmpty();
    }

    @Test
    @DisplayName("position white pieces correctly")
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
    @DisplayName("position black pieces correctly")
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
    @DisplayName("allow piece to be move")
    void pieceCanBeMoved() {
        Board newBoard = newGameBoard.moveTo(E2, E4);
        assertThat(newBoard.at(E2)).isEmpty();
        assertThat(newBoard.at(E4)).contains(new Piece(WHITE, PAWN));
    }

    @Test
    @DisplayName("not allow empty place to move")
    void emptyPlaceCannotBeMoved() {
        assertThrows(Exception.class, () -> newGameBoard.moveTo(E5, E6));
    }

    @Test
    @DisplayName("not allow to move to occupied position")
    void canNotBeMoveToOccupiedPosition() {
        assertThrows(Exception.class, () -> newGameBoard.moveTo(A1, A2));
    }

    @Test
    @DisplayName("allow a pawn to be promoted to a queen")
    void promoteToQueen() {
        Board b = Board.empty().placeAt(new Piece(WHITE, PAWN), A7).promote(A7, A8).get();
        assertThat(b.at(A8)).contains(WHITE.queen());
    }

    @Test
    @DisplayName("provide occupation map")
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

    @Nested
    @DisplayName("detect")
    class Detect {

        @Nested
        @DisplayName("automatic draw")
        class AutomaticDraw {

            @Nested
            @DisplayName("by lack of pieces")
            class LackOfPieces {
                @Test
                void empty() {
                    assertThat(Board.empty().autodraw()).isTrue();
                }

                @Test
                @DisplayName("new")
                void newBoard() {
                    assertThat(new Board().autodraw()).isFalse();
                }

                @Test
                void opened() {
                    RichGame game = new RichGame().playMoves(Pair.of(E2, E4), Pair.of(C7, C5), Pair.of(C2, C3), Pair.of(D7, D5), Pair.of(E4, D5));
                    assertThat(game.board.autodraw()).isFalse();
                }

                @Test
                @DisplayName("two kings")
                void twoKings() {
                    Board board = visual.str2Obj("""
        k
  K      """);
                    assertThat(board.autodraw()).isTrue();
                }

                @Test
                @DisplayName("two kings and one pawn")
                void twoKingsPawn() {
                    Board board = visual.str2Obj("""
    P   k
  K      """);
                    assertThat(board.autodraw()).isFalse();
                }

                @Test
                @DisplayName("two kings and one bishop")
                void twoKingsBishop() {
                    Board board = visual.str2Obj("""
        k
  K     B""");
                    assertThat(board.autodraw()).isTrue();
                }

                @Test
                @DisplayName("two kings, one bishop and one knight of different colors")
                void twoKingsBishopOpKnight() {
                    Board board = visual.str2Obj("""
        k
  K n   B""");
                    assertThat(board.autodraw()).isTrue();
                }

                @Test
                @DisplayName("two kings, one bishop and one knight of same color")
                void twoKingsBishopKnight() {
                    Board board = visual.str2Obj("""
    B   k
  K N    """);
                    assertThat(board.autodraw()).isFalse();
                }

                @Test
                @DisplayName("two kings, one bishop and one rook of different colors")
                void twoKingsBishopOpRook() {
                    Board board = visual.str2Obj("""
        k
  K r   B""");
                    assertThat(board.autodraw()).isFalse();
                }
            }

            @Nested
            @DisplayName("by fifty moves")
            class FiftyMoves {

                @Test
                @DisplayName("new")
                void newBoard() {
                    assertThat(new Board().autodraw()).isFalse();
                }

                @Test
                void opened() {
                    RichGame game = new RichGame().playMoves(Pair.of(E2, E4), Pair.of(C7, C5), Pair.of(C2, C3), Pair.of(D7, D5), Pair.of(E4, D5));
                    assertThat(game.board.autodraw()).isFalse();
                }

                @Test
                @DisplayName("tons of pointless moves")
                void manyPointless() {
                    List<Pair<Pos, Pos>> movesCycle = List.of(Pair.of(B1, C3), Pair.of(B8, C6), Pair.of(C3, B1), Pair.of(C6, B8));
                    List<Pair<Pos, Pos>> moves = Collections.nCopies(30, movesCycle)
                        .stream()
                        .flatMap(List::stream)
                        .toList();
                    RichGame game = new RichGame().playMoves(moves.toArray(new Pair[0]));
                    assertThat(game.board.autodraw()).isTrue();
                }
            }
        }
    }
}

package leo.lija.chess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Pos.A7;
import static leo.lija.chess.Pos.A8;
import static leo.lija.chess.Pos.C1;
import static leo.lija.chess.Pos.C2;
import static leo.lija.chess.Pos.D1;
import static leo.lija.chess.Role.KNIGHT;
import static leo.lija.chess.Role.QUEEN;

@DisplayName("pawn promotion should")
class PromotionTest extends BaseChess {

    Board board = visual.str2Obj("""
  p
K      """);
    RichGame game = new RichGame(board, BLACK);

    @Test
    @DisplayName("promote to a queen")
    void promoteToQueen() {
        beGame(game.playMove(C2, C1, QUEEN), """

K q    """);
    }

    @Test
    @DisplayName("promote to a queen by default")
    void promoteToQueenByDefault() {
        beGame(game.playMove(C2, C1), """

K q    """);
    }

    @Test
    @DisplayName("promote to a knight")
    void promoteToKnight() {
        beGame(game.playMove(C2, C1, KNIGHT), """

K n    """);
    }

    @Test
    @DisplayName("promote to a queen by killing")
    void promoteToQueenByKilling() {
        beGame(new RichGame(visual.str2Obj("""
  p
K  R"""), BLACK).playMove(C2, D1), """

K  q""");
    }

    @Test
    @DisplayName("promote to a knight by killing")
    void promoteToKnightByKilling() {
        beGame(new RichGame(visual.str2Obj("""
  p
K  R"""), BLACK).playMove(C2, D1, KNIGHT), """

K  n""");
    }

    @Test
    @DisplayName("promote to a white knight")
    void promoteToWhiteKnight() {
        beGame(new RichGame(visual.str2Obj("""

P





K n    """)).playMove(A7, A8, KNIGHT), """
N






K n    """);
    }
}

package leo.lija.chess;

import leo.lija.chess.format.VisualFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Pos.C1;
import static leo.lija.chess.Pos.C2;
import static leo.lija.chess.Role.KNIGHT;
import static leo.lija.chess.Role.QUEEN;

@DisplayName("pawn promotion should")
class PromotionTest {

    VisualFormat visual = new VisualFormat();
    Utils utils = new Utils();

    Board board = visual.str2Obj("""
  p
K      """);
    Game game = new Game(board, BLACK);

    @Test
    @DisplayName("promote to a queen")
    void promoteToQueen() {
        utils.beGame(game.playMove(C2, C1, QUEEN), """

K q    """);
    }

    @Test
    @DisplayName("promote to a queen by default")
    void promoteToQueenByDefault() {
        utils.beGame(game.playMove(C2, C1), """

K q    """);
    }

    @Test
    @DisplayName("promote to a knight")
    void promoteToKnight() {
        utils.beGame(game.playMove(C2, C1, KNIGHT), """

K n    """);
    }
}

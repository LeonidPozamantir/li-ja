package leo.lija;

import leo.lija.format.VisualFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static leo.lija.Color.BLACK;
import static leo.lija.Pos.C1;
import static leo.lija.Pos.C2;
import static leo.lija.Role.KNIGHT;
import static leo.lija.Role.QUEEN;

@DisplayName("pawn promotion should")
class PromotionTest {

    VisualFormat visual = new VisualFormat();
    Utils utils = new Utils();

    Situation situation = visual.str2Obj("""
  p
K      """).as(BLACK);

    @Test
    @DisplayName("promote to a queen")
    void promoteToQueen() {
        utils.beSituation(situation.playMove(C2, C1, QUEEN), """

K q    """);
    }

    @Test
    @DisplayName("promote to a queen by default")
    void promoteToQueenByDefault() {
        utils.beSituation(situation.playMove(C2, C1), """

K q    """);
    }

    @Test
    @DisplayName("promote to a knight")
    void promoteToKnight() {
        utils.beSituation(situation.playMove(C2, C1, KNIGHT), """

K n    """);
    }
}

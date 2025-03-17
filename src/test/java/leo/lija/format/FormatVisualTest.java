package leo.lija.format;


import leo.lija.model.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static leo.lija.model.Pos.A6;
import static leo.lija.model.Pos.B3;
import static leo.lija.model.Pos.B5;
import static leo.lija.model.Pos.D3;
import static leo.lija.model.Pos.D5;
import static leo.lija.model.Pos.E6;
import static leo.lija.model.Pos.F7;
import static leo.lija.model.Pos.G8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIterable;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class FormatVisualTest {

    private Visual visual = new Visual();

    private final String newBoardFormat = """
rnbqkbnr
pppppppp




PPPPPPPP
RNBQKBNR
""";

    private List<String> examples = List.of(newBoardFormat, """
rnbqkp r
pppppppp



    P n
PPPP   P
RNBQK  R
""", """
       k
       P





K
""", """
rnbqkbnr
pppppppp





RK    NR
""", """
  bqkb r
p ppp pp
pr
   P p
   QnB
 PP  N
P    PPP
RN  K  R
""", """
r   k nr
pp n ppp
  p p
q
 b P B
P N  Q P
 PP BPP
R   K  R
""");


    private String newLine(String str) {
        return str + "\n";
    }

    @Nested
    @DisplayName("The visual formatter")
    class VisualFormatterTest {

        @Test
        @DisplayName("can export a new board")
        void exportNewBoard() {
            assertThat(newLine(visual.obj2Str(new Board()))).isEqualTo(newBoardFormat);
        }

        @Test
        @DisplayName("can import a new board")
        void importNewBoard() {
            assertThat(visual.str2Obj(newBoardFormat)).isEqualTo(new Board());
        }

        @Test
        @DisplayName("import and export is non destructive")
        void nonDestructive() {
            assertThatIterable(examples).isNotEmpty();
            assertThat(examples).allMatch(e -> newLine(visual.obj2Str(visual.str2Obj(e))).equals(e));
        }

        @Test
        @DisplayName("export with special marks")
        void exportWithMarks() {
            Board board = visual.str2Obj("""
k B



N B    P

PPPPPPPP
 NBQKBNR
""");
            assertThat(newLine(visual.obj2StrWithMarks(board, Map.of(Set.of(B3, D3, B5, D5, A6, E6, F7, G8), 'x'))))
                .isEqualTo("""
k B   x
     x
x   x
 x x
N B    P
 x x
PPPPPPPP
 NBQKBNR
""");

        }

    }

}

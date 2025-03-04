package leo.lija.format;


import leo.lija.model.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIterable;

import java.util.List;



public class FormatVisualTest {

    private Visual visual = new Visual();

    private List<String> examples = List.of("""
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

    private final String newBoardFormat = """
rnbqkbnr
pppppppp




PPPPPPPP
RNBQKBNR
""";

    private String newLine(String str) {
        return str + "\n";
    }

    @Nested
    @DisplayName("The visual formatter")
    class VisualFormatterTest {

        @Test
        @DisplayName("can export a new board")
        void exportNewBoard() {
            assertThat(newLine(visual.Obj2Str(new Board()))).isEqualTo(newBoardFormat);
        }

        @Test
        @DisplayName("import and export is non destructive")
        void nonDestructive() {
            assertThatIterable(examples).isNotEmpty();
            assertThat(examples).allMatch(e -> newLine(visual.Obj2Str(visual.str2Obj(e))).equals(e));
        }
    }

}

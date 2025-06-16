package leo.lija;

import leo.lija.format.VisualFormat;

import static org.assertj.core.api.Assertions.assertThat;

public class Utils {

    VisualFormat visual = new VisualFormat();

    public void beSituation(Situation situation, String visualString) {
        assertThat(visual.obj2Str(situation.board))
            .isEqualTo(visual.obj2Str(visual.str2Obj(visualString)));
    }
}

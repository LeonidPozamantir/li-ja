package leo.lija.app.entities.event;

import leo.lija.chess.Pos;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
public class CheckEvent implements Event {
    private Pos pos;

    @Override
    public String typ() {
        return "check";
    }

    @Override
    public String data() {
        return pos.key();
    }
}

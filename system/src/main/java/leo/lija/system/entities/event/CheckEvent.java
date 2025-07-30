package leo.lija.system.entities.event;

import leo.lija.chess.Pos;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class CheckEvent implements Event {
    private Pos pos;

    @Override
    public String encode() {
        return "C" + pos.getPiotr();
    }
}

package leo.lija.system.entities.event;

import leo.lija.chess.Pos;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class EnpassantEvent implements Event {
    private Pos killed;

    @Override
    public String encode() {
        return "E" + killed.getPiotr();
    }
}

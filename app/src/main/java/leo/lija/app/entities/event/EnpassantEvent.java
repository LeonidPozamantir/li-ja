package leo.lija.app.entities.event;

import leo.lija.chess.Pos;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class EnpassantEvent implements Event {
    private Pos killed;

    @Override
    public String typ() {
        return "enpassant";
    }

    @Override
    public String data() {
        return killed.key();
    }
}

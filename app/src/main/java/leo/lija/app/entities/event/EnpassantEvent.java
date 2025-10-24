package leo.lija.app.entities.event;

import leo.lija.chess.Pos;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
public class EnpassantEvent implements Event {
    private Pos killed;

    @Override
    public String encode() {
        return "E" + killed.getPiotr();
    }

    @Override
    public Map<String, Object> export() {
        return Map.of(
            "type", "enpassant",
            "killed", killed.key()
        );
    }
}

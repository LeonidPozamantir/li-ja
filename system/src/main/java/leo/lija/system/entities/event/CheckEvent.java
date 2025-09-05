package leo.lija.system.entities.event;

import leo.lija.chess.Pos;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
public class CheckEvent implements Event {
    private Pos pos;

    @Override
    public String encode() {
        return "C" + pos.getPiotr();
    }

    @Override
    public Map<String, Object> export() {
        return Map.of(
            "type", "check",
            "key", pos.key()
        );
    }
}

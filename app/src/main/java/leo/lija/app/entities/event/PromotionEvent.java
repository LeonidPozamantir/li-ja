package leo.lija.app.entities.event;

import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
public class PromotionEvent implements Event {
    private Role role;
    private Pos pos;

    @Override
    public String typ() {
        return "promotion";
    }

    @Override
    public Map<String, Object> data() {
        return Map.of(
            "key", pos.key(),
            "pieceClass", role.toString().toLowerCase()
        );
    }
}

package leo.lija.system.entities.event;

import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class PromotionEvent implements Event {
    private Role role;
    private Pos pos;

    @Override
    public String encode() {
        return "P" + role.fen + pos.getPiotr();
    }
}

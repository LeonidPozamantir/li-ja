package leo.lija.system.entities.event;

import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.system.Piotr;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
public class PromotionEvent implements Event {
    private Role role;
    private Pos pos;

    @Override
    public Optional<String> encode() {
        return Optional.ofNullable(Piotr.encodePos.get(pos))
            .map(p -> "P" + role.fen + p);
    }
}

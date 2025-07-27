package leo.lija.system.entities.event;

import leo.lija.chess.Pos;
import leo.lija.system.Piotr;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
public class CheckEvent implements Event {
    private Pos pos;

    @Override
    public Optional<String> encode() {
        return Optional.ofNullable(Piotr.encodePos.get(pos))
            .map(p -> "C" + p);
    }
}

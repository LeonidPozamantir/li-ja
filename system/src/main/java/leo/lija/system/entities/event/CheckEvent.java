package leo.lija.system.entities.event;

import leo.lija.chess.Pos;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
public class CheckEvent implements Event {
    private Pos pos;

    @Override
    public Optional<String> encode() {
        return Optional.of("C" + pos.getPiotr());
    }
}

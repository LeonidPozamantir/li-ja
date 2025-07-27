package leo.lija.system.entities.event;

import lombok.EqualsAndHashCode;

import java.util.Optional;

@EqualsAndHashCode
public class EndEvent implements Event {
    @Override
    public Optional<String> encode() {
        return Optional.of("e");
    }
}

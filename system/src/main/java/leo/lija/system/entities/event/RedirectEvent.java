package leo.lija.system.entities.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
public class RedirectEvent implements Event {
    private String url;

    @Override
    public Optional<String> encode() {
        return Optional.of("r" + url);
    }
}

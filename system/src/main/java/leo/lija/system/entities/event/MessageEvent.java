package leo.lija.system.entities.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
public class MessageEvent implements Event {
    private String author;
    private String message;

    @Override
    public Optional<String> encode() {
        return Optional.of("M" + author + " " + message.replaceAll("\\|", "(pipe)"));
    }
}

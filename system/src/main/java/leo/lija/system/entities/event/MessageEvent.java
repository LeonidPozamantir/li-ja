package leo.lija.system.entities.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
public class MessageEvent implements Event {
    private String author;
    private String message;

    @Override
    public String encode() {
        return "M" + author + " " + message.replace("|", "(pipe)");
    }

    @Override
    public Map<String, Object> export() {
        return Map.of(
            "type", "message",
            "message", List.of(author, message)
        );
    }
}

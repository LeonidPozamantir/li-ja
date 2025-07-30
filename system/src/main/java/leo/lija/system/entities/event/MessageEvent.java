package leo.lija.system.entities.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class MessageEvent implements Event {
    private String author;
    private String message;

    @Override
    public String encode() {
        return "M" + author + " " + message.replace("|", "(pipe)");
    }
}

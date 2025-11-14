package leo.lija.app.entities.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class MessageEvent implements Event {
    private String author;
    private String message;

    @Override
    public String typ() {
        return "message";
    }

    @Override
    public Map<String, Object> data() {
        return null;
    }

    @Override
    public boolean owner() {
        return true;
    }
}

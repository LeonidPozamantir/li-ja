package leo.lija.system.entities.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
public class RedirectEvent implements Event {
    private String url;

    @Override
    public String encode() {
        return "r" + url;
    }

    @Override
    public Map<String, Object> export() {
        return Map.of(
            "type", "redirect",
            "url", url
        );
    }
}

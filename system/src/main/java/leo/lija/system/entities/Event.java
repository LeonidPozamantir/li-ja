package leo.lija.system.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Event {
    private final String tpe;
    private Map<String, Object> data = Map.of();
}

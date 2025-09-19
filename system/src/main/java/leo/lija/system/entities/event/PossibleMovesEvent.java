package leo.lija.system.entities.event;

import leo.lija.chess.Pos;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@EqualsAndHashCode
public class PossibleMovesEvent implements Event {
    private Map<Pos, List<Pos>> moves;

    @Override
    public String encode() {
        return "p" + moves.entrySet().stream()
            .map(e -> Stream.concat(Stream.of(e.getKey()), e.getValue().stream()).map(Pos::getPiotr).map(String::valueOf).collect(Collectors.joining()))
            .collect(Collectors.joining(","));
    }

    @Override
    public Map<String, Object> export() {
        if (moves.isEmpty()) return Map.of("type", "possible_moves");
        return Map.of(
            "type", "possible_moves",
            "possible_moves", moves.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().key(), e -> e.getValue().stream().map(Pos::key).collect(Collectors.joining())))
        );
    }
}

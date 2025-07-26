package leo.lija.system.entities.event;

import leo.lija.chess.Pos;
import leo.lija.system.Piotr;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@EqualsAndHashCode
public class PossibleMovesEvent implements Event {
    private Map<Pos, List<Pos>> moves;

    @Override
    public Optional<String> encode() {
        return Optional.of("p" + moves.entrySet().stream()
            .map(e -> {
                Pos orig = e.getKey();
                List<Pos> dests = e.getValue();
                char o = Piotr.encodePos.get(orig);
                List<Character> ds = dests.stream().map(d -> Piotr.encodePos.get(d)).toList();
                return o + ds.stream().map(c -> c.toString()).collect(Collectors.joining());
            }).collect(Collectors.joining(",")));
    }
}

package leo.lija.system.entities.event;

import leo.lija.chess.Pos;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PossibleMovesEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        return Optional.of(new PossibleMovesEvent(
            Arrays.stream(str.split(","))
                .filter(s -> s.length() >= 2)
                .map(line -> {
                    char o = line.charAt(0);
                    String ds = line.substring(1);
                    Pos orig = Pos.piotr(o).get();
                    List<Pos> dests = ds.chars().mapToObj(c -> (char) c).map(c -> Pos.piotr(c).get()).toList();
                    return Map.entry(orig, dests);
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        ));
    }
}

package leo.lija.system.entities.event;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        LinkedList<String> words = new LinkedList<>(List.of(str.split(" ")));
        if (words.size() < 2) return Optional.empty();
        String author = words.get(0);
        words.removeFirst();
        return Optional.of(new MessageEvent(
            author, words.stream().collect(Collectors.joining(" ")).replaceAll("\\(pipe\\)", "|")
        ));
    }
}

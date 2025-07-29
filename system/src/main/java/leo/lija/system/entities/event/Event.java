package leo.lija.system.entities.event;

import leo.lija.chess.Move;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public interface Event {
    Optional<String> encode();

    public static List<Event> fromMove(Move move) {
        return Stream.of(
            MoveEvent.apply(move),
            move.enpassant() ? move.capture().map(EnpassantEvent::new).orElse(null) : null,
            move.promotion().map(role -> new PromotionEvent(role, move.dest())).orElse(null)
        ).filter(Objects::nonNull).toList();
    }
}

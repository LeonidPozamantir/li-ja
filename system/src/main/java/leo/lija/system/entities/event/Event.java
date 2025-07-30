package leo.lija.system.entities.event;

import leo.lija.chess.Move;
import leo.lija.chess.utils.Pair;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public interface Event {
    String encode();

    public static List<Event> fromMove(Move move) {
        return Stream.of(
            MoveEvent.apply(move),
            move.enpassant() ? move.capture().map(EnpassantEvent::new).orElse(null) : null,
            move.promotion().map(role -> new PromotionEvent(role, move.dest())).orElse(null),
            move.castle().map(rook -> new CastlingEvent(Pair.of(move.orig(), move.dest()), rook, move.color())).orElse(null)
        ).filter(Objects::nonNull).toList();
    }
}

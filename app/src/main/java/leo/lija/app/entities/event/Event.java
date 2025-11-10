package leo.lija.app.entities.event;

import leo.lija.chess.Color;
import leo.lija.chess.Move;
import leo.lija.chess.Situation;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public interface Event {
    String typ();
    Object data();
    default Optional<Color> only() {
        return Optional.empty();
    }

    static List<Event> fromMove(Move move) {
        return Stream.of(
            MoveEvent.apply(move),
            move.enpassant() ? move.capture().map(EnpassantEvent::new).orElse(null) : null,
            move.promotion().map(role -> new PromotionEvent(role, move.dest())).orElse(null),
            move.castle().map(kingAndRook -> new CastlingEvent(kingAndRook.getFirst(), kingAndRook.getSecond(), move.color())).orElse(null)
        ).filter(Objects::nonNull).toList();
    }

    static List<Event> fromSituation(Situation situation) {
        return Stream.of(
            situation.check() ? situation.kingPos().map(CheckEvent::new).orElse(null) : null,
            situation.threefoldRepetition() ? new ThreefoldEvent() : null
        ).filter(Objects::nonNull).toList();
    }

    static PossibleMovesEvent possibleMoves(Situation situation, Color color) {
        return new PossibleMovesEvent(
            color,
            color == situation.getColor() ? situation.destinations() : Map.of()
        );
    }
}

package leo.lija.system.entities.event;

import leo.lija.chess.Color;
import leo.lija.chess.Move;
import leo.lija.chess.Situation;
import leo.lija.chess.utils.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public interface Event {
    String encode();
    Map<String, Object> export();

    static List<Event> fromMove(Move move) {
        return Stream.of(
            MoveEvent.apply(move),
            move.enpassant() ? move.capture().map(EnpassantEvent::new).orElse(null) : null,
            move.promotion().map(role -> new PromotionEvent(role, move.dest())).orElse(null),
            move.castle().map(rook -> new CastlingEvent(Pair.of(move.orig(), move.dest()), rook, move.color())).orElse(null)
        ).filter(Objects::nonNull).toList();
    }

    static List<Event> fromSituation(Situation situation) {
        return Stream.of(
            situation.check() ? situation.kingPos().map(CheckEvent::new).orElse(null) : null,
            situation.end() ? new EndEvent() : null,
            situation.threefoldRepetition() ? new ThreefoldEvent() : null
        ).filter(Objects::nonNull).toList();
    }

    static PossibleMovesEvent possibleMoves(Situation situation, Color color) {
        return new PossibleMovesEvent(color == situation.getColor() ? situation.destinations() : Map.of());
    }
}

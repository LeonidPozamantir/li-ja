package leo.lija.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import leo.lija.chess.Color;
import leo.lija.chess.Piece;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Data
public class DbPlayer {

    @NotNull
    @Column(nullable = false)
    private String id;

    @NotNull
    @Column(nullable = false)
    private String color;

    @NotNull
    @Column(nullable = false)
    private String ps;

    private Integer aiLevel;
    private Boolean isWinner;
    private String evts;
    private Integer elo;

    public DbPlayer copy() {
        return new DbPlayer(id, color, ps, aiLevel, isWinner, evts, elo);
    }

    public EventStack eventStack() {
        return EventStack.decode(evts);
    }

    public String newEvts(List<Event> events) {
        return eventStack().withEvents(events).optimize().encode();
    }

    public boolean isAi() {
        return aiLevel != null;
    }

    public static String encodePieces(Map<Pos, Piece> pieces, io.vavr.collection.List<Pair<Pos, Piece>> deads, Color color) {
        return Stream.concat(
            pieces.entrySet().stream()
                .filter(e -> e.getValue().is(color))
                .map(e -> String.valueOf(e.getKey().getPiotr()) + e.getValue().role().fen),
            deads.toJavaStream()
                .filter(p -> p.getSecond().is(color))
                .map(p -> String.valueOf(p.getFirst().getPiotr()) + Character.toUpperCase(p.getSecond().role().fen))
        ).collect(Collectors.joining(" "));
    }

}

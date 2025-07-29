package leo.lija.system.entities.event;

import leo.lija.chess.Color;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.system.Piotr;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
public class MoveEvent implements Event {
    private Pos orig;
    private Pos dest;
    private Color color;

    @Override
    public Optional<String> encode() {
        return Optional.ofNullable(Piotr.encodePos.get(orig))
            .flatMap(o -> Optional.ofNullable(Piotr.encodePos.get(dest))
                .map(d -> "m" + o + d + color.getLetter()));
    }

    public static MoveEvent apply(Move move) {
        return new MoveEvent(move.orig(), move.dest(), move.piece().color());
    }
}

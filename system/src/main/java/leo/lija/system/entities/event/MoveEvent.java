package leo.lija.system.entities.event;

import leo.lija.chess.Color;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class MoveEvent implements Event {
    private Pos orig;
    private Pos dest;
    private Color color;

    @Override
    public String encode() {
        return "m" + orig.getPiotr() + dest.getPiotr() + color.getLetter();
    }

    public static MoveEvent apply(Move move) {
        return new MoveEvent(move.orig(), move.dest(), move.piece().color());
    }
}

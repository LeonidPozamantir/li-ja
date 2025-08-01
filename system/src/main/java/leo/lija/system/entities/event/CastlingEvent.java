package leo.lija.system.entities.event;

import leo.lija.chess.Color;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public class CastlingEvent implements Event {
    private Pair<Pos, Pos> king;
    private Pair<Pos, Pos> rook;
    private Color color;

    @Override
    public String encode() {
        return "c" + king.getFirst().getPiotr() + king.getSecond().getPiotr() + rook.getFirst().getPiotr() + rook.getSecond().getPiotr() + color.getLetter();
    }
}

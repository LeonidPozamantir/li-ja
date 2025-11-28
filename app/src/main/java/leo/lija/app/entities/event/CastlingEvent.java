package leo.lija.app.entities.event;

import leo.lija.chess.Color;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
public class CastlingEvent implements Event {
    private Pair<Pos, Pos> king;
    private Pair<Pos, Pos> rook;
    private Color color;

    @Override
    public String typ() {
        return "castling";
    }

    @Override
    public Map<String, Object> data() {
        return Map.of(
            "king", List.of(king.getFirst().key(), king.getSecond().key()),
            "rook", List.of(rook.getFirst().key(), rook.getSecond().key()),
            "color", color.name()
        );
    }
}

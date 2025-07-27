package leo.lija.system.entities.event;

import leo.lija.chess.Color;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;
import leo.lija.system.Piotr;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
public class CastlingEvent implements Event {
    private Pair<Pos, Pos> king;
    private Pair<Pos, Pos> rook;
    private Color color;

    @Override
    public Optional<String> encode() {
        return Optional.ofNullable(Piotr.encodePos.get(king.getFirst()))
            .flatMap(k1 -> Optional.ofNullable(Piotr.encodePos.get(king.getSecond()))
                .flatMap(k2 -> Optional.ofNullable(Piotr.encodePos.get(rook.getFirst()))
                    .flatMap(r1 -> Optional.ofNullable(Piotr.encodePos.get(rook.getSecond()))
                        .map(r2 -> "c" + k1 + k2 + r1 + r2 + color.getLetter()))));
    }
}

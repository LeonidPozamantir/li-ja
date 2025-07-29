package leo.lija.system.entities.event;

import leo.lija.chess.Color;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;

import java.util.Optional;

public class CastlingEventDecoder implements EventDecoder {
    @Override
    public Optional<Event> decode(String str) {
        char[] chars = str.toCharArray();
        if (chars.length != 5) return Optional.empty();
        char k1 = chars[0];
        char k2 = chars[1];
        char r1 = chars[2];
        char r2 = chars[3];
        char c = chars[4];
        return Pos.piotr(k1)
            .flatMap(king1 -> Pos.piotr(k2)
                .flatMap(king2 -> Pos.piotr(r1)
                    .flatMap(rook1 -> Pos.piotr(r2)
                        .flatMap(rook2 -> Color.apply(c)
                            .map(color -> new CastlingEvent(Pair.of(king1, king2), Pair.of(rook1, rook2), color))))));
    }
}

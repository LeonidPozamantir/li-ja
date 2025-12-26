package leo.lija.chess.format;

import leo.lija.chess.Move;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class PgnReader {

    public List<Move> apply(String pgn) {
        List<San> sans = SanParser.apply(pgn);
        return List.of();
    }
}

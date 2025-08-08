package leo.lija.chess;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class Clock {
    private final Color color;
    private final int increment;
    private final int limit;
    private final float whiteTime;
    private final float blackTime;

    public Map<Color, Float> times() {
        return Map.of(WHITE, whiteTime, BLACK, blackTime);   // TODO: consider making lazy
    }

}

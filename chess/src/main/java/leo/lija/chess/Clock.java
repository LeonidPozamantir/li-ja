package leo.lija.chess;

import java.util.Map;

public record Clock(
    Color color,
    int increment,
    int limit,
    Map<Color, Float> times
) {
}

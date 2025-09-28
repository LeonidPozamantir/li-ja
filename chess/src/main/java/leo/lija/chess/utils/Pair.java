package leo.lija.chess.utils;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
public class Pair<L, R> {
    L first;
    R second;

    @Override
    public String toString() {
        return first + ", " + second;
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

}
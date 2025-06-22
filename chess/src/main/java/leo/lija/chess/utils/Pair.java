package leo.lija.chess.utils;

import lombok.Value;

@Value
public class Pair<L, R> {
    L first;
    R second;

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }
}
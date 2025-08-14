package leo.lija.system.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum Variant {
    STANDARD(1), CHESS960(2);

    @Getter
    private final int value;

    public static int toInt(Variant s) {
        return s.value;
    }

    public static final Map<Integer, Variant> indexed = Collections.unmodifiableMap(Arrays.stream(values()).collect(Collectors.toMap(Variant::toInt, Function.identity())));

    public static Optional<Variant> fromInt(int i) {
        return Optional.ofNullable(indexed.get(i));
    }

}

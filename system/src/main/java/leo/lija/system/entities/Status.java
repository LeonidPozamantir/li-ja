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
public enum Status {
    CREATED(10), STARTED(20), ABORTED(25), MATE(30), RESIGN(31), STALEMATE(32), TIMEOUT(33), DRAW(34), OUTOFTIME(35), CHEAT(36);

    @Getter
    private final int value;

    public static int toInt(Status s) {
        return s.value;
    }

    public static final Map<Integer, Status> indexed = Collections.unmodifiableMap(Arrays.stream(values()).collect(Collectors.toMap(Status::toInt, Function.identity())));

    public static Optional<Status> fromInt(int i) {
        return Optional.ofNullable(indexed.get(i));
    }

}

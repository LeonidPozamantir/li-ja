package leo.lija.system.entities;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Status(int id) {

    public static final Status CREATED = new Status(10);
    public static final Status STARTED = new Status(20);
    public static final Status ABORTED = new Status(25);
    public static final Status MATE = new Status(30);
    public static final Status RESIGN = new Status(31);
    public static final Status STALEMATE = new Status(32);
    public static final Status TIMEOUT = new Status(33);
    public static final Status DRAW = new Status(34);
    public static final Status OUTOFTIME = new Status(35);
    public static final Status CHEAT = new Status(36);

    private static final List<Status> all = List.of(CREATED, STARTED, ABORTED, MATE, RESIGN, STALEMATE, TIMEOUT, DRAW, OUTOFTIME, CHEAT);

    public static final Map<Integer, Status> byId = all.stream().collect(Collectors.toMap(Status::id, Function.identity()));

    public static Optional<Status> apply(Integer id) {
        return Optional.ofNullable(byId.get(id));
    }
}

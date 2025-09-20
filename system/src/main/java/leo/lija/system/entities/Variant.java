package leo.lija.system.entities;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Variant(int id, String string) {

    @Override
    public String toString() {
        return string;
    }

    public static final Variant STANDARD = new Variant(1, "Standard");
    public static final Variant CHESS960 = new Variant(2, "Chess960");

    public static final List<Variant> all = List.of(STANDARD, CHESS960);

    public static final Map<Integer, Variant> byId = all.stream().collect(Collectors.toMap(Variant::id, Function.identity()));

    public static Optional<Variant> apply(Integer id) {
        return Optional.ofNullable(byId.get(id));
    }
}

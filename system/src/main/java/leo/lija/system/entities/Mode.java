package leo.lija.system.entities;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public record Mode(int id, String string) {

    @Override
    public String toString() {
        return string;
    }

    public static final Mode CASUAL = new Mode(0, "Casual");
    public static final Mode RATED = new Mode(1, "Rated");

    public static final List<Mode> all = List.of(CASUAL, RATED);

    public static final Map<Integer, Mode> byId = all.stream().collect(Collectors.toMap(Mode::id, Function.identity()));

    public static Optional<Mode> apply(Integer id) {
        return Optional.ofNullable(byId.get(id));
    }
}

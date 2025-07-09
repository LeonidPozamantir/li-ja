package leo.lija.system;

import java.util.Set;

public record GameVariant(int id, String name) {
    public static final Set<GameVariant> values = Set.of(
        new GameVariant(1, "variant_standard"),
        new GameVariant(2, "variant_960")
    );
}

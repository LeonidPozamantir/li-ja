package leo.lija.app;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record MoveForm(@NotNull String from, @NotNull String to, @Nullable String promotion, @Nullable Integer b) {
}

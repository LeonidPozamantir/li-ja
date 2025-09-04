package leo.lija.app;

import jakarta.validation.constraints.NotNull;

public record LijaForm(@NotNull String from, @NotNull String to) {
}

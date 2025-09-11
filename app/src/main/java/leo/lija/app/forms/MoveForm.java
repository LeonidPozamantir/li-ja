package leo.lija.app.forms;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;

public record MoveForm(@NotEmpty String from, @NotEmpty String to, @Nullable String promotion, @Nullable Integer b) {
}

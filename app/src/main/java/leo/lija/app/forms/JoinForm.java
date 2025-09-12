package leo.lija.app.forms;

import jakarta.validation.constraints.NotEmpty;

public record JoinForm(@NotEmpty String redirect, @NotEmpty String messages) {
}

package leo.lija.app.forms;

import jakarta.validation.constraints.NotEmpty;

public record LobbyJoinForm(@NotEmpty String entry, @NotEmpty String messages) {
}

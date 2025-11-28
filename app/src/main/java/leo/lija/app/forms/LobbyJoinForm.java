package leo.lija.app.forms;

import jakarta.validation.constraints.NotEmpty;

import java.util.Optional;

public record LobbyJoinForm(@NotEmpty String entry, @NotEmpty String messages, @NotEmpty String hook, Optional<@NotEmpty String> myHook) {
}

package leo.lija.app.forms;

import jakarta.validation.constraints.NotEmpty;
import leo.lija.system.entities.entry.EntryGame;

public record JoinForm(@NotEmpty String redirect, @NotEmpty String messages, EntryGame entry) {
}

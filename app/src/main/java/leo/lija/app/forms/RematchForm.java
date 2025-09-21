package leo.lija.app.forms;

import jakarta.validation.constraints.NotEmpty;
import leo.lija.system.entities.entry.EntryGame;

public record RematchForm(@NotEmpty String whiteRedirect, @NotEmpty String blackRedirect, EntryGame entry) {
}

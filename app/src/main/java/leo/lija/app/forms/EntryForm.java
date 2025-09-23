package leo.lija.app.forms;

import jakarta.validation.constraints.NotEmpty;

public record EntryForm(@NotEmpty String entry) {
}

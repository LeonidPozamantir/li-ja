package leo.lija.app.forms;

import jakarta.validation.constraints.NotEmpty;

public record TalkForm(@NotEmpty String author, @NotEmpty String message) {
}

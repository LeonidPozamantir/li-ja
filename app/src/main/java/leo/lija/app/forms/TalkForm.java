package leo.lija.app.forms;

import jakarta.validation.constraints.NotEmpty;

public record TalkForm(@NotEmpty String message) {
}

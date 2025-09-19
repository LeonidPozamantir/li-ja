package leo.lija.app.forms;

import jakarta.validation.constraints.NotEmpty;

public record MessagesForm(@NotEmpty String messages) {
}

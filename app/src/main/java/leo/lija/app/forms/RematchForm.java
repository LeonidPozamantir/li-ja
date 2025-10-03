package leo.lija.app.forms;

import jakarta.validation.constraints.NotEmpty;

public record RematchForm(
        @NotEmpty String whiteRedirect,
        @NotEmpty String blackRedirect,
        @NotEmpty String entry,
        @NotEmpty String messages
) {
}

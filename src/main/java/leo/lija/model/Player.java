package leo.lija.model;

import org.springframework.data.annotation.Id;

public record Player(
        @Id
        String id,
        String color,
        String ps
) {
}

package leo.lija.system.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public record Player(
        String id,
        String color,
        String ps
) {
}

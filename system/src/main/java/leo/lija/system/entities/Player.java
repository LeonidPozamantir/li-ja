package leo.lija.system.entities;

import jakarta.persistence.Embeddable;
import org.springframework.data.annotation.Id;

@Embeddable
public record Player(
        String id,
        String color,
        String ps
) {
}

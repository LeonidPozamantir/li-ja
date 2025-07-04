package leo.lija.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Embeddable
public record Player(
    @NotNull
    @Column(nullable = false)
    String id,
    @NotNull
    @Column(nullable = false)
    String color,
    @NotNull
    @Column(nullable = false)
    String ps,
    Integer aiLevel,
    Boolean isWinner,
    String evts,
    Integer elo
) {

    public boolean isAi() {
        return aiLevel != null;
    }
}

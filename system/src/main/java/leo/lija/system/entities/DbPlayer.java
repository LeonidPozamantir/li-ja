package leo.lija.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Getter
public class DbPlayer {

    @NotNull
    @Column(nullable = false)
    private String id;

    @NotNull
    @Column(nullable = false)
    private String color;

    @NotNull
    @Column(nullable = false)
    private String ps;

    private Integer aiLevel;
    private Boolean isWinner;
    private String evts;
    private Integer elo;

    public boolean isAi() {
        return aiLevel != null;
    }
}

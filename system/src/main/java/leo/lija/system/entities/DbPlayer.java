package leo.lija.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Data
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

    public EventStack eventStack() {
        return EventStack.decode(evts);
    }

    public boolean isAi() {
        return aiLevel != null;
    }

}

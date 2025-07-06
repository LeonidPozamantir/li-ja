package leo.lija.system.entities;


import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Getter
public class Game {

    @Id
    private String id;

    @ElementCollection
    private List<Player> players;

    @NotNull
    @Column(nullable = false)
    private String pgn;
    private int status;
    private int turns;
    private int variant;

    public leo.lija.chess.Game toChess() {
        return new leo.lija.chess.Game(null, null);
    }

}

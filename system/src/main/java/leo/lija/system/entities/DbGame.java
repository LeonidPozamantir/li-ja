package leo.lija.system.entities;


import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import leo.lija.chess.Board;
import leo.lija.chess.Game;
import leo.lija.chess.History;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.A1;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Getter
public class DbGame {

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

    public Game toChess() {
        return new Game(
            new Board(Map.of(A1, WHITE.rook()), new History()),
            0 == turns % 2 ? WHITE : BLACK,
            pgn
            );
    }

}

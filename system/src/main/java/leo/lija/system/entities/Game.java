package leo.lija.system.entities;


import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public record Game(
    @Id
    String id,
    @ElementCollection
    List<Player> players,
    @NotNull
    @Column(nullable = false)
    String pgn,
    int status,
    int turns,
    int variant
) {}

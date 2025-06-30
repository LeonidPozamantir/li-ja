package leo.lija.system.entities;


import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.List;

@Entity
public record Game(
    @Id
    String id,
    @ElementCollection
    List<Player> players
) {}

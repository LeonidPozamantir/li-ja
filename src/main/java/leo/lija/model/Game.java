package leo.lija.model;


import org.springframework.data.annotation.Id;

import java.util.List;

public record Game(
        @Id
        String id,
        List<Player> players
) {}

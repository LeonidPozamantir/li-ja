package leo.lija.model;


import org.springframework.data.annotation.Id;

public record Game(
        @Id
        String id
) {}

package leo.lija.system.dto;

import leo.lija.system.entities.entry.EntryPlayer;

import java.util.List;

public record EntryDto(
    String id,
    List<EntryPlayer> players,
    String variant,
    String rated,
    String clock
) {
}

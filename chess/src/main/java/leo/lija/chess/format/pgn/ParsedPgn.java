package leo.lija.chess.format.pgn;

import java.util.List;

public record ParsedPgn(List<Tag> tags, List<San> sans) {
}

package leo.lija.chess.format;

import leo.lija.chess.Role;

import java.util.Optional;

public record Suffixes(boolean check, boolean checkmate, Optional<Role> promotion) {
}

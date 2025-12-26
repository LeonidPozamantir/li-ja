package leo.lija.chess.format;

import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class Std implements San {

    private final Pos dest;
    private final boolean capture;
    private final Optional<Role> role;
    private final Optional<Character> file;
    private final Optional<Character> rank;
    private final boolean check;
    private final boolean checkmate;
    private final Optional<Role> promotion;

    public Std(Pos dest, Boolean capture, Optional<Role> role, Optional<Character> file, Optional<Character> rank) {
        this(dest, capture, role, file, rank, false, false, Optional.empty());
    }

    public Std withSuffixes(Suffixes s) {
        return new Std(dest, capture, role, file, rank, s.check(), s.checkmate(), s.promotion());
    }
}

package leo.lija.chess.format.pgn;

import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.exceptions.ChessException;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class Std implements San {

    private final Pos dest;
    private final Role role;
    private final boolean capture;
    private final Optional<Integer> file;
    private final Optional<Integer> rank;
    private final boolean check;
    private final boolean checkmate;
    private final Optional<Role> promotion;

    public Std(Pos dest, Role role, Boolean capture, Optional<Integer> file, Optional<Integer> rank) {
        this(dest, role, capture, file, rank, false, false, Optional.empty());
    }

    public Std(Pos dest, Role role, Boolean capture) {
        this(dest, role, capture, Optional.empty(), Optional.empty());
    }

    public Std withSuffixes(Suffixes s) {
        return new Std(dest, role, capture, file, rank, s.check(), s.checkmate(), s.promotion());
    }

    private <A> boolean compare(Optional<A> a, A b) {
        return a.map(av -> av == b).orElse(true);
    }

    @Override
    public Move apply(Game game) {
        List<Move> potentialMoves = game.situation().moves().values().stream()
            .map(moves -> moves.stream()
                .filter(move -> move.dest() == dest && move.piece().role() == role)
                .findFirst()
            ).filter(Optional::isPresent)
            .map(Optional::get)
            .filter(m -> compare(file, m.orig().getX()) && compare(rank, m.orig().getY()))
            .toList();
        if (potentialMoves.isEmpty()) throw new ChessException("No move found: %s\n%s".formatted(this, game.getBoard()));
        if (potentialMoves.size() > 1) throw new ChessException("Many moves found: %s\n%s".formatted(this, game.getBoard()));
        return potentialMoves.getFirst();
    }
}

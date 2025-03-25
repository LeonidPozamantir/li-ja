package leo.lija.model;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static leo.lija.model.Color.BLACK;
import static leo.lija.model.Color.WHITE;

public record Piece(Color color, Role role) {

    public Set<Pos> basicMoves(Pos pos, Board board) {
        Set<Pos> friends = board.occupation().get(color);
        Set<Pos> enemies = board.occupation().get(color.getOpposite());

        if (role == Role.PAWN) {
           Function<Pos, Optional<Pos>> dir = color == Color.WHITE ? Pos::up : Pos::down;
           return dir.apply(pos).map(next -> {
                    boolean notMoved = (color == WHITE && pos.getY() == 2) || (color == BLACK && pos.getY() == 7);
                    Optional<Pos> one = Optional.of(next).filter(p -> !board.occupations().contains(p));
                    List<Optional<Pos>> optPositions = List.of(
                        one,
                        notMoved ? one.flatMap(o -> dir.apply(o).filter(p -> !board.occupations().contains(p))) : Optional.empty(),
                        next.left().filter(enemies::contains),
                        next.right().filter(enemies::contains)
                    );
                    return optPositions.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
                }
            ).orElse(Set.of());
        }
        else if (role.trajectory) {
            return new Trajectory(role.dirs, friends, enemies).from(pos);
        } else {
            Set<Pos> allPoss = role.dirs.stream()
                .map(dir -> dir.apply(pos))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toSet());
            allPoss.removeAll(friends);
            return allPoss;
        }
    }

    public boolean is(Color color) {
        return color == this.color;
    }

    public boolean is(Role role) {
        return role == this.role;
    }

    @Override
    public String toString() {
        return (color + "-" + role).toLowerCase();
    }

    public char fen() {
        return color == Color.WHITE ? Character.toUpperCase(role.fen) : role.fen;
    }

}

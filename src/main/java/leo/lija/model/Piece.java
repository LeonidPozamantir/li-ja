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
            boolean notMoved = (color == WHITE && pos.getY() == 2) || (color == BLACK && pos.getY() == 7);
            Function<Pos, Optional<Pos>> dir = color == Color.WHITE ? Pos::up : Pos::down;
           return dir.apply(pos).map(one -> {
                    List<Optional<Pos>> optPositions = List.of(
                        Optional.of(one).filter(p -> !friends.contains(p)),
                        notMoved ? dir.apply(one).filter(p -> !friends.contains(p)) : Optional.empty(),
                        one.left().filter(enemies::contains),
                        one.right().filter(enemies::contains)
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

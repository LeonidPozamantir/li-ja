package leo.lija.model;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public record Piece(Color color, Role role) {

    public Set<Pos> basicMoves(Pos pos, Board board) {
        Set<Pos> friends = board.occupation().get(color);
        Set<Pos> enemies = board.occupation().get(color.getOpposite());

        if (role.trajectory) {
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

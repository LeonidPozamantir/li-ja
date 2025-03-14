package leo.lija.model;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static leo.lija.model.Role.ROOK;

public record Piece(Color color, Role role) {

    public Set<Pos> basicMoves(Pos pos, Board board) {
        Set<Pos> friends = board.occupation().get(color);
        Set<Pos> enemies = board.occupation().get(color.getOpposite());

        return switch (role) {
            case ROOK -> new Trajectories(ROOK.dirs, friends, enemies).from(pos);
            default -> Set.of();
        };
    }

    public boolean is(Color color) {
        return color == this.color;
    }

    public boolean is(Role role) {
        return role == this.role;
    }

    @Override
    public String toString() {
        return (color + " " + role).toLowerCase();
    }

    public char fen() {
        return color == Color.WHITE ? Character.toUpperCase(role.fen) : role.fen;
    }

    @RequiredArgsConstructor
    private class Trajectories {

        private final List<Function<Pos, Optional<Pos>>> dirs;
        private final Set<Pos> friends;
        private final Set<Pos> enemies;
        public Set<Pos> from(Pos pos) {
            return dirs.stream().flatMap(dir -> forward(pos, dir).stream()).collect(Collectors.toSet());
        }

        public List<Pos> forward(Pos p, Function<Pos, Optional<Pos>> dir) {
            List<Pos> res = new ArrayList<>();
            Optional<Pos> next = dir.apply(p);
            while (next.isPresent() && !friends.contains(next.get())) {
                res.add(next.get());
                if (enemies.contains(next.get())) break;
                next = dir.apply(next.get());
            }
            return res;
        }
    }
}

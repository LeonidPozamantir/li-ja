package leo.lija.model;

import java.util.Set;

public record Piece(Color color, Role role) {

    public Set<Pos> basicMoves(Pos pos, Board board) {
        return Set.of();
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
}

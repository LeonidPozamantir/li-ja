package leo.lija.model;

public record Piece(Color color, Role role) {

    @Override
    public String toString() {
        return (color + " " + role).toLowerCase();
    }

    public char fen() {
        return color == Color.WHITE ? Character.toUpperCase(role.fen) : role.fen;
    }
}

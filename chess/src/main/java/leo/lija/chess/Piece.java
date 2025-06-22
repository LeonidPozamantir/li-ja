package leo.lija.chess;

public record Piece(Color color, Role role) {

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

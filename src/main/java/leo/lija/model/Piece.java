package leo.lija.model;

public record Piece(Color color, Role role) {

    @Override
    public String toString() {
        return color + " " + role;
    }
}

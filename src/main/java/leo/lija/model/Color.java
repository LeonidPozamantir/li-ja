package leo.lija.model;

import lombok.Getter;

import java.util.List;

import static leo.lija.model.Role.BISHOP;
import static leo.lija.model.Role.KING;
import static leo.lija.model.Role.KNIGHT;
import static leo.lija.model.Role.PAWN;
import static leo.lija.model.Role.QUEEN;
import static leo.lija.model.Role.ROOK;

public enum Color {
    WHITE,
    BLACK;

    public Piece of(Role role) {
        return new Piece(this, role);
    }
    public static Color isW(boolean b) {
        return b ? WHITE : BLACK;
    }

    @Getter
    private Color opposite;

    static {
        WHITE.opposite = BLACK;
        BLACK.opposite = WHITE;
    }

    public Piece pawn() {
        return new Piece(this, PAWN);
    }
    public Piece knight() {
        return new Piece(this, KNIGHT);
    }
    public Piece bishop() {
        return new Piece(this, BISHOP);
    }
    public Piece rook() {
        return new Piece(this, ROOK);
    }
    public Piece queen() {
        return new Piece(this, QUEEN);
    }
    public Piece king() {
        return new Piece(this, KING);
    }

    public static List<Color> all() {
        return List.of(WHITE, BLACK);
    }
}

package leo.lija;

import lombok.Getter;

import java.util.List;

import static leo.lija.Role.BISHOP;
import static leo.lija.Role.KING;
import static leo.lija.Role.KNIGHT;
import static leo.lija.Role.PAWN;
import static leo.lija.Role.QUEEN;
import static leo.lija.Role.ROOK;

@Getter
public enum Color {
    WHITE(2, 5, 7),
    BLACK(7, 4, 2);

    public Piece of(Role role) {
        return new Piece(this, role);
    }
    public static Color isW(boolean b) {
        return b ? WHITE : BLACK;
    }

    private Color opposite;
    private int unmovedPawnY;
    private int passablePawnY;
    private int promotablePawnY;

    Color(int unmovedPawnY, int passablePawnY, int promotablePawnY) {
        this.unmovedPawnY = unmovedPawnY;
        this.passablePawnY = passablePawnY;
        this.promotablePawnY = promotablePawnY;
    }

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

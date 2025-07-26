package leo.lija.chess;

import lombok.Getter;

import java.util.List;
import java.util.Optional;

import static leo.lija.chess.Role.BISHOP;
import static leo.lija.chess.Role.KING;
import static leo.lija.chess.Role.KNIGHT;
import static leo.lija.chess.Role.PAWN;
import static leo.lija.chess.Role.QUEEN;
import static leo.lija.chess.Role.ROOK;

@Getter
public enum Color {
    WHITE(2, 5, 7, 'w'),
    BLACK(7, 4, 2, 'b');

    public Piece of(Role role) {
        return new Piece(this, role);
    }

    private Color opposite;
    private int unmovedPawnY;
    private int passablePawnY;
    private int promotablePawnY;
    private char letter;

    Color(int unmovedPawnY, int passablePawnY, int promotablePawnY, char letter) {
        this.unmovedPawnY = unmovedPawnY;
        this.passablePawnY = passablePawnY;
        this.promotablePawnY = promotablePawnY;
        this.letter = letter;
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


    public static Color apply(boolean b) {
        return b ? WHITE : BLACK;
    }

    public static Optional<Color> apply(String n) {
        return switch (n) {
            case "white" -> Optional.of(WHITE);
            case "black" -> Optional.of(BLACK);
            default -> Optional.empty();
        };
    }

    public static Optional<Color> apply(char c) {
        return switch (c) {
            case 'w' -> Optional.of(WHITE);
            case 'b' -> Optional.of(BLACK);
            default -> Optional.empty();
        };
    }

    public static final List<Color> all = List.of(WHITE, BLACK);
}

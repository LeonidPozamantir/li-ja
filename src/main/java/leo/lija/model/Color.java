package leo.lija.model;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;

public enum Color {
    WHITE,
    BLACK;

    public static Color isW(boolean b) {
        return b ? WHITE : BLACK;
    }

    @Getter
    private Color opposite;

    static {
        WHITE.opposite = BLACK;
        BLACK.opposite = WHITE;
    }

    public static List<Color> all() {
        return List.of(WHITE, BLACK);
    }
}

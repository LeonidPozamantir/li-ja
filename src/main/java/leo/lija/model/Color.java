package leo.lija.model;

import java.util.List;

public enum Color {
    WHITE,
    BLACK;

    public static Color isW(boolean b) {
        return b ? WHITE : BLACK;
    }

    public Color opposite;

    static {
        WHITE.opposite = BLACK;
        BLACK.opposite = WHITE;
    }

    public static List<Color> all() {
        return List.of(WHITE, BLACK);
    }
}

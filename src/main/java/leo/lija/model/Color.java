package leo.lija.model;

public enum Color {
    WHITE,
    BLACK;

    public static Color isW(boolean b) {
        return b ? WHITE : BLACK;
    }
}

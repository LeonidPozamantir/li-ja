package leo.lija.format;

import leo.lija.entities.Game;

public interface Format {
    public Game str2Game(String str);
    public String game2Str(Game game);
}

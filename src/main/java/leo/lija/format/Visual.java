package leo.lija.format;

import leo.lija.model.Game;

public class Visual implements Format{

    @Override
    public Game str2Game(String str) {
        return new Game("fromsource", null);
    }

    @Override
    public String game2Str(Game game) {
        return "";
    }
}

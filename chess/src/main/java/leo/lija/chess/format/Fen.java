package leo.lija.chess.format;

import leo.lija.chess.Game;

public class Fen implements Format<Game> {

    @Override
    public Game str2Obj(String source) {
        return new Game();
    }

    @Override
    public String obj2Str(Game game) {
        return "";
    }
}

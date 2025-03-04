package leo.lija.format;

import leo.lija.entities.Game;

public interface Format<T> {
    public T str2Obj(String source);
    public String Obj2Str(T game);
}

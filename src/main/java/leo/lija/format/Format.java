package leo.lija.format;

public interface Format<T> {
    public T str2Obj(String source);
    public String obj2Str(T game);
}

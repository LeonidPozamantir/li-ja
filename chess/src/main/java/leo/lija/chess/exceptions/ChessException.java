package leo.lija.chess.exceptions;

public class ChessException extends RuntimeException {
    public ChessException(String message) {
        super(message);
    }
    public ChessException(String message, Exception e) {
        super(message, e);
    }
}

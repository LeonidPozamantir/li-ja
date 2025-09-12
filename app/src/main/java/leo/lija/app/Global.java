package leo.lija.app;

import jakarta.servlet.http.HttpServletRequest;
import leo.lija.chess.exceptions.ChessException;
import leo.lija.system.exceptions.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class Global {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFoundException(HttpServletRequest request) {
        return new ResponseEntity<>("Not found " + request.getRequestURI(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(Exception e) {
        return ResponseEntity.badRequest().body("Invalid form: " + e.getMessage());
    }

    @ExceptionHandler({AppException.class, ChessException.class})
    public ResponseEntity<String> handleAppException(AppException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleInternalErrorException(Exception e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}

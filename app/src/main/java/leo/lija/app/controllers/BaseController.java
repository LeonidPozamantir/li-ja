package leo.lija.app.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class BaseController {
    protected ResponseEntity<Void> validRedir(Runnable op, String url) {
        try {
            op.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/" + url))
                .build();
    }
}

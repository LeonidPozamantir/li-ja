package leo.lija.app.controllers;

import leo.lija.system.LobbyXhr;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("lobby")
@RequiredArgsConstructor
public class LobbyXhrC {

    private final LobbyXhr xhr;

    @PostMapping("/cancel/{ownerId}")
    public ResponseEntity<Void> cancel(@PathVariable String ownerId) {
        xhr.cancel(ownerId);
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create("/"))
            .build();
    }

}

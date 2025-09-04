package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.app.LijaForm;
import leo.lija.chess.utils.Pair;
import leo.lija.system.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Application {

    private final Server server;

    @PostMapping("/move/{fullId}")
    public ResponseEntity move(@PathVariable String fullId, @Valid @RequestBody LijaForm move, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid move");
        }

        server.play(fullId, Pair.of(move.from(), move.to()));

        return ResponseEntity.ok().body("all right");
    }
}

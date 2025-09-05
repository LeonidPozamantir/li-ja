package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.app.MoveForm;
import leo.lija.system.Server;
import leo.lija.system.Syncer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class Application {

    private final Server server;
    private final Syncer syncer;

    @PostMapping("/move/{fullId}")
    public ResponseEntity<String> move(@PathVariable String fullId, @Valid @RequestBody MoveForm move, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid move");
        }

        server.play(fullId, move.from(), move.to(), Optional.ofNullable(move.promotion()));

        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/sync/{id}/{color}/{version}/{fullId}")
    public Map<String, Object> sync(@PathVariable String id, @PathVariable String color, @PathVariable Integer version, @PathVariable String fullId) {
        return syncer.sync(id, color, version, fullId);
    }
}

package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.app.forms.MoveForm;
import leo.lija.system.Pinger;
import leo.lija.system.Server;
import leo.lija.system.Syncer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class Application {

    private final Server server;
    private final Syncer syncer;
    private final Pinger pinger;

    @GetMapping("/sync/{gameId}/{color}/{version}/{fullId}")
    public Map<String, Object> sync(@PathVariable String gameId, @PathVariable String color, @PathVariable Integer version, @PathVariable String fullId) {
        return syncer.sync(gameId, color, version, Optional.of(fullId));
    }

    @GetMapping("/sync/{gameId}/{color}/{version}")
    public Map<String, Object> syncPublic(@PathVariable String gameId, @PathVariable String color, @PathVariable Integer version) {
        return syncer.sync(gameId, color, version, Optional.empty());
    }

    @PostMapping("/move/{fullId}")
    public ResponseEntity<String> move(@PathVariable String fullId, @Valid @RequestBody MoveForm move, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid move");
        }

        server.play(fullId, move.from(), move.to(), Optional.ofNullable(move.promotion()));

        return ResponseEntity.ok().body("ok");
    }

    @GetMapping("/ping")
    public Map<String, Object> ping(
        @RequestParam Optional<String> username,
        @RequestParam("player_key") Optional<String> playerKey,
        @RequestParam Optional<String> watcher,
        @RequestParam("get_nb_watchers") Optional<String> getNbWatchers
    ) {
        return pinger.ping(username, playerKey, watcher, getNbWatchers);
    }

}

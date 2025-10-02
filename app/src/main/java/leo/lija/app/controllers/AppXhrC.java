package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.app.forms.MoveForm;
import leo.lija.system.Pinger;
import leo.lija.system.AppXhr;
import leo.lija.system.AppSyncer;
import leo.lija.system.memo.AliveMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AppXhrC extends BaseController {

    private final AppXhr xhr;
    private final AppSyncer syncer;
    private final Pinger pinger;
    private final AliveMemo aliveMemo;

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

        xhr.play(fullId, move.from(), move.to(), Optional.ofNullable(move.promotion()));

        return ResponseEntity.ok().body("ok");
    }

    @GetMapping("/abort/{fullId}")
    public ResponseEntity<Void> abort(@PathVariable String fullId) {
        return validRedir(() -> xhr.abort(fullId), fullId);
    }

    @PostMapping("/outoftime/{fullId}")
    public ResponseEntity<Void> outoftime(@PathVariable String fullId) {
        return validRedir(() -> xhr.outoftime(fullId), fullId);
    }

    @GetMapping("/resign/{fullId}")
    public ResponseEntity<Void> resign(@PathVariable String fullId) {
        return validRedir(() -> xhr.resign(fullId), fullId);
    }

    @GetMapping("/force-resign/{fullId}")
    public ResponseEntity<Void> forceResign(@PathVariable String fullId) {
        return validRedir(() -> xhr.forceResign(fullId), fullId);
    }

    @GetMapping("/claim-draw/{fullId}")
    public ResponseEntity<Void> claimDraw(@PathVariable String fullId) {
        return validRedir(() -> xhr.claimDraw(fullId), fullId);
    }

    @GetMapping("/ping")
    public Map<String, Object> ping(
        @RequestParam Optional<String> username,
        @RequestParam("player_key") Optional<String> playerKey,
        @RequestParam Optional<String> watcher,
        @RequestParam("get_nb_watchers") Optional<String> getNbWatchers,
        @RequestParam("hook_id") Optional<String> hookId
    ) {
        return pinger.ping(username, playerKey, watcher, getNbWatchers, hookId);
    }

    @GetMapping({"/how-many-players-now", "/internal/nb-players"})
    public String nbPlayers() {
        return String.valueOf(aliveMemo.count());
    }

}

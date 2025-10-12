package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.app.forms.MoveForm;
import leo.lija.app.forms.TalkForm;
import leo.lija.system.AppSyncer;
import leo.lija.system.AppXhr;
import leo.lija.system.Pinger;
import leo.lija.system.memo.AliveMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
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
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class AppXhrC extends BaseController {

    private final AppXhr xhr;
    private final AppSyncer syncer;
    private final Pinger pinger;
    private final AliveMemo aliveMemo;
    private final TaskExecutor executor;

    @GetMapping("/sync/{gameId}/{color}/{version}/{fullId}")
    public Map<String, Object> sync(@PathVariable String gameId, @PathVariable String color, @PathVariable Integer version, @PathVariable String fullId) {
        return syncAll(gameId, color, version, Optional.of(fullId));
    }

    @GetMapping("/sync/{gameId}/{color}/{version}")
    public Map<String, Object> syncPublic(@PathVariable String gameId, @PathVariable String color, @PathVariable Integer version) {
        return syncAll(gameId, color, version, Optional.empty());
    }

    private Map<String, Object> syncAll(String gameId, String color, Integer version, Optional<String> fullId) {
        return CompletableFuture.supplyAsync(() -> syncer.sync(gameId, color, version, fullId), executor).join();
    }

    @PostMapping("/move/{fullId}")
    public ResponseEntity<String> move(@PathVariable String fullId, @Valid @RequestBody MoveForm move, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid move");
        }

        return CompletableFuture.supplyAsync(() -> {
            xhr.play(fullId, move.from(), move.to(), Optional.ofNullable(move.promotion()));
            return ResponseEntity.ok().body("ok");
        }).join();

    }

    @GetMapping("/abort/{fullId}")
    public ResponseEntity<Void> abort(@PathVariable String fullId) {
        return validRedir(() -> xhr.abort(fullId), fullId);
    }

    @PostMapping("/outoftime/{fullId}")
    public void outoftime(@PathVariable String fullId) {
        xhr.outoftime(fullId);
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

    @GetMapping("/draw-accept/{fullId}")
    public ResponseEntity<Void> drawAccept(@PathVariable String fullId) {
        return validRedir(() -> xhr.drawAccept(fullId), fullId);
    }

    @PostMapping("/talk/{fullId}")
    public void talk(@PathVariable String fullId, @Valid @RequestBody TalkForm talkForm) {
        xhr.talk(fullId, talkForm.message());
    }

    @PostMapping("/moretime/{fullId}")
    public float moretime(@PathVariable String fullId) {
        float time = xhr.moretime(fullId);
        return time;
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
    public long nbPlayers() {
        return aliveMemo.count();
    }

}

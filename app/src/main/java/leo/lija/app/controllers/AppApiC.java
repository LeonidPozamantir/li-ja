package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.app.forms.EntryForm;
import leo.lija.app.forms.JoinForm;
import leo.lija.app.forms.RematchForm;
import leo.lija.app.AppApi;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AppApiC {

    private final AppApi api;
    private final TaskExecutor executor;

    @GetMapping("/show/{fullId}")
    public Map<String, Object> show(@PathVariable String fullId) {
        return CompletableFuture.supplyAsync(() -> api.show(fullId), executor).join();
    }

    @PostMapping("/reload-table/{gameId}")
    public void reloadTable(@PathVariable String gameId) {
        api.reloadTable(gameId);
    }

    @PostMapping("/alive/{gameId}/{color}")
    public void alive(@PathVariable String gameId, @PathVariable String color) {
        api.alive(gameId, color);
    }

    @PostMapping("/start/{gameId}")
    public void start(@PathVariable String gameId, @Valid @RequestBody EntryForm entryData) {
        api.start(gameId, entryData.entry());
    }

    @PostMapping("/join/{fullId}")
    public ResponseEntity<String> join(@PathVariable String fullId, @Valid @RequestBody JoinForm join) {
        api.join(fullId, join.redirect(), join.messages(), join.entry());
        return ResponseEntity.ok().body("ok");
    }

    @GetMapping("/activity/{gameId}/{color}")
    public int activity(@PathVariable String gameId, @PathVariable String color) {
        return api.isConnected(gameId, color) ? 1 : 0;
    }

    @GetMapping("/game-version/{gameId}")
    public int gameVersion(@PathVariable String gameId) {
        return CompletableFuture.supplyAsync(() -> api.gameVersion(gameId), executor).join();
    }

    @PostMapping("/rematch-accept/{gameId}/{color}/{newGameId}")
    public void rematchAccept(@PathVariable String gameId, @PathVariable String color, @PathVariable String newGameId, @Valid @RequestBody RematchForm r) {
        api.rematchAccept(gameId, newGameId, color, r.whiteRedirect(), r.blackRedirect(), r.entry(), r.messages());
    }

}

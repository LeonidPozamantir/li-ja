package leo.lija.app.controllers;

import io.vavr.Tuple3;
import jakarta.validation.Valid;
import leo.lija.app.AppApi;
import leo.lija.app.Captcha;
import leo.lija.app.forms.EntryForm;
import leo.lija.app.forms.JoinForm;
import leo.lija.app.forms.RematchForm;
import leo.lija.chess.Color;
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
    private final Captcha captcha;

    @GetMapping("/show/{fullId}")
    public CompletableFuture<Map<String, Object>> show(@PathVariable String fullId) {
        return CompletableFuture.supplyAsync(() -> api.show(fullId), executor);
    }

    @PostMapping("/reload-table/{gameId}")
    public void reloadTable(@PathVariable String gameId) {
        api.reloadTable(gameId);
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

    @PostMapping("/adjust/{username}")
    public void adjust(@PathVariable String username) {
        api.adjust(username);
    }

    public Map<String, String> captcha() {
        Tuple3<String, String, Color> data = captcha.create();
        return Map.of(
            "id", data._1,
            "fen", data._2,
            "color", data._3.toString()
        );
    }
}

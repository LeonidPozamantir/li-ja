package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.app.forms.EntryForm;
import leo.lija.app.forms.JoinForm;
import leo.lija.app.forms.MessagesForm;
import leo.lija.app.forms.RematchForm;
import leo.lija.app.forms.TalkForm;
import leo.lija.system.AppApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AppApiC {

    private final AppApi api;

    @PostMapping("/talk/{gameId}")
    public ResponseEntity<String> talk(@PathVariable String gameId, @Valid @RequestBody TalkForm talk) {
        api.talk(gameId, talk.author(), talk.message());
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/update-version/{gameId}")
    public void updateVersion(@PathVariable String gameId) {
        api.updateVersion(gameId);
    }

    @PostMapping("/reload-table/{gameId}")
    public void reloadTable(@PathVariable String gameId) {
        api.reloadTable(gameId);
    }

    @PostMapping("/alive/{gameId}/{color}")
    public void alive(@PathVariable String gameId, @PathVariable String color) {
        api.alive(gameId, color);
    }

    @PostMapping("/draw/{gameId}/{color}")
    public void draw(@PathVariable String gameId, @PathVariable String color, @Valid @RequestBody MessagesForm msgs) {
        api.draw(gameId, color, msgs.messages());
    }

    @PostMapping("/draw-accept/{gameId}/{color}")
    public void drawAccept(@PathVariable String gameId, @PathVariable String color, @Valid @RequestBody MessagesForm msgs) {
        api.drawAccept(gameId, color, msgs.messages());
    }

    @PostMapping("/end/{gameId}")
    public void end(@PathVariable String gameId, @Valid @RequestBody MessagesForm msgs) {
        api.end(gameId, msgs.messages());
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
    public String activity(@PathVariable String gameId, @PathVariable String color) {
        return String.valueOf(api.activity(gameId, color));
    }

    @PostMapping("/rematch-accept/{gameId}/{color}/{newGameId}")
    public void rematchAccept(@PathVariable String gameId, @PathVariable String color, @PathVariable String newGameId, @Valid @RequestBody RematchForm r) {
        api.acceptRematch(gameId, newGameId, color, r.whiteRedirect(), r.blackRedirect(), r.entry());
    }

}

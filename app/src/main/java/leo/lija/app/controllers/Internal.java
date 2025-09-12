package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.app.forms.JoinForm;
import leo.lija.app.forms.TalkForm;
import leo.lija.system.InternalApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Internal {

    private final InternalApi api;

    @PostMapping("/internal/talk/{gameId}")
    public ResponseEntity<String> talk(@PathVariable String gameId, @Valid @RequestBody TalkForm talk, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid talk");
        }
        api.talk(gameId, talk.author(), talk.message());
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/internal/update-version/{gameId}")
    public void updateVersion(@PathVariable String gameId) {
        api.updateVersion(gameId);
    }

    @PostMapping("/internal/end-game/{gameId}")
    public void endGame(@PathVariable String gameId) {
        api.endGame(gameId);
    }

    @PostMapping("/internal/join/{fullId}")
    public ResponseEntity<String> join(@PathVariable String fullId, @Valid @RequestBody JoinForm join, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid join");
        }
        api.join(fullId, join.redirect(), join.messages());
        return ResponseEntity.ok().body("ok");
    }
}

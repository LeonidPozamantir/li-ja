package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.app.forms.EndForm;
import leo.lija.app.forms.JoinForm;
import leo.lija.app.forms.RematchForm;
import leo.lija.app.forms.TalkForm;
import leo.lija.system.InternalApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Internal {

    private final InternalApi api;

    @PostMapping("/internal/talk/{gameId}")
    public ResponseEntity<String> talk(@PathVariable String gameId, @Valid @RequestBody TalkForm talk) {
        api.talk(gameId, talk.author(), talk.message());
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/internal/update-version/{gameId}")
    public void updateVersion(@PathVariable String gameId) {
        api.updateVersion(gameId);
    }

    @PostMapping("/internal/reload-table/{gameId}")
    public void reloadTable(@PathVariable String gameId) {
        api.reloadTable(gameId);
    }

    @PostMapping("/internal/end/{gameId}")
    public void end(@PathVariable String gameId, @Valid @RequestBody EndForm msgs) {
        api.end(gameId, msgs.messages());
    }

    @PostMapping("/internal/join/{fullId}")
    public ResponseEntity<String> join(@PathVariable String fullId, @Valid @RequestBody JoinForm join) {
        api.join(fullId, join.redirect(), join.messages());
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/internal/accept-rematch/{gameId}")
    public void acceptRematch(@PathVariable String gameId, @Valid @RequestBody RematchForm rematch) {
        api.acceptRematch(gameId, rematch.whiteRedirect(), rematch.blackRedirect());
    }
}

package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.app.forms.LobbyJoinForm;
import leo.lija.app.lobby.Api;
import leo.lija.app.lobby.Preload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class LobbyC extends BaseController {

    private final Api api;
    private final Preload preloader;

    @PostMapping("/lobby/cancel/{ownerId}")
    public ResponseEntity<Void> cancel(@PathVariable String ownerId) {
        api.cancel(ownerId);
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create("/"))
            .build();
    }

    @GetMapping("/api/lobby/preload")
    public Map<String, Object> preload(@RequestParam Optional<Integer> auth, @RequestParam Optional<Integer> chat, @RequestParam Optional<String> myHookId) {
        return preloader.apply(
            auth.orElse(0) == 1,
            chat.orElse(0) == 1,
            get(myHookId)
        );
    }

    @PostMapping("/api/lobby/join/{gameId}/{color}")
    public void join(@PathVariable String gameId, @PathVariable String color, @Valid @RequestBody LobbyJoinForm lobbyJoinForm) {
        api.join(gameId, color, lobbyJoinForm.entry(), lobbyJoinForm.messages());
    }

    @PostMapping("/api/lobby/create/{hookOwnerId}")
    public void create(@PathVariable String hookOwnerId) {
        api.create(hookOwnerId);
    }

}

package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.app.forms.LobbyJoinForm;
import leo.lija.system.LobbyApi;
import leo.lija.system.LobbyPreloader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/lobby")
@RequiredArgsConstructor
public class LobbyApiC {

    private final LobbyApi api;
    private final LobbyPreloader preloader;

    @GetMapping("/preload")
    public Map<String, Object> preload(@RequestParam Optional<Integer> auth, @RequestParam Optional<Integer> chat, @RequestParam Optional<String> myHookId) {
        return preloader.apply(
            auth.orElse(0) == 1,
            chat.orElse(0) == 1,
            myHookId.filter(h -> !h.isEmpty())
        );
    }

    @PostMapping("/join/{gameId}/{color}")
    public void join(@PathVariable String gameId, @PathVariable String color, @Valid @RequestBody LobbyJoinForm lobbyJoinForm) {
        api.join(gameId, color, lobbyJoinForm.entry(), lobbyJoinForm.messages());
    }

    @PostMapping("/create/{hookOwnerId}")
    public void create(@PathVariable String hookOwnerId) {
        api.create(hookOwnerId);
    }

    @PostMapping("/alive/{hookOwnerId}")
    public void alive(@PathVariable String hookOwnerId) {
        api.alive(hookOwnerId);
    }

}

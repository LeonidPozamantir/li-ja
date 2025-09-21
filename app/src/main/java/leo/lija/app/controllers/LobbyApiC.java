package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.system.LobbyApi;
import leo.lija.system.entities.entry.EntryGame;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/lobby")
@RequiredArgsConstructor
public class LobbyApiC {

    private final LobbyApi api;

    @PostMapping("/join/{gameId}/{color}")
    public void join(@PathVariable String gameId, @PathVariable String color, @Valid @RequestBody EntryGame ec) {
        api.join(gameId, color, ec);
    }

    @PostMapping("/create/{hookOwnerId}")
    public void create(@PathVariable String hookOwnerId) {
        api.create(hookOwnerId);
    }

    @PostMapping("/remove/{hookId}")
    public void remove(@PathVariable String hookId) {
        api.remove(hookId);
    }

    @PostMapping("/alive/{hookOwnerId}")
    public void alive(@PathVariable String hookOwnerId) {
        api.alive(hookOwnerId);
    }
}

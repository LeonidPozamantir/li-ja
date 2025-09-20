package leo.lija.app.controllers;

import jakarta.validation.Valid;
import leo.lija.system.LobbyApi;
import leo.lija.system.entities.Hook;
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
    public void join(@PathVariable String gameId, @PathVariable String color) {
        api.join(gameId, color);
    }

    @PostMapping("/inc")
    public void inc() {
        api.inc();
    }

    @PostMapping("/create")
    public void create(@Valid @RequestBody Hook hook) {
        api.create(hook);
    }
}

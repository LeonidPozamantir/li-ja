package leo.lija.app.controllers;

import leo.lija.system.LobbyApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
}

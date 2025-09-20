package leo.lija.app.controllers;

import leo.lija.system.LobbySyncer;
import leo.lija.system.LobbyXhr;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("lobby")
@RequiredArgsConstructor
public class LobbyXhrC {

    private final LobbyXhr xhr;
    private final LobbySyncer syncer;

    @GetMapping({"/sync/{hookId}", "/sync", "/api/lobby/preload/{hookId}", "/api/lobby/preload"})
    public Map<String, Object> sync(
        @PathVariable Optional<String> hookId,
        @RequestParam Optional<Integer> auth,
        @RequestParam Optional<Integer> state
    ) {
        return syncer.sync(
            hookId,
            auth.orElse(0) == 1,
            state.orElse(0)
        );
    }
}

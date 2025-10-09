package leo.lija.app.controllers;

import leo.lija.system.LobbySyncer;
import leo.lija.system.LobbyXhr;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("lobby")
@RequiredArgsConstructor
public class LobbyXhrC {

    private final LobbyXhr xhr;
    private final LobbySyncer syncer;
    private final TaskExecutor executor;

    @GetMapping({"/sync/{hookId}", "/sync", "/api/lobby/preload/{hookId}", "/api/lobby/preload"})
    public Map<String, Object> sync(
        @PathVariable Optional<String> hookId,
        @RequestParam Optional<Integer> auth,
        @RequestParam Optional<Integer> state,
        @RequestParam Optional<Integer> messageId,
        @RequestParam Optional<Integer> entryId
    ) {
        return CompletableFuture.supplyAsync(() -> syncer.sync(
            hookId,
            auth.orElse(0) == 1,
            state.orElse(0),
            messageId.orElse(-1),
            entryId.orElse(0)
        ), executor).join();
    }
}

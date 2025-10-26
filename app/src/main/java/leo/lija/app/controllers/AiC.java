package leo.lija.app.controllers;

import leo.lija.system.ai.CraftyServer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class AiC {

    private final TaskExecutor executor;
    private final CraftyServer craftyServer;

    @GetMapping("/ai")
    public ResponseEntity<String> ai(@RequestParam Optional<String> fen, @RequestParam Optional<Integer> level) {
        return CompletableFuture.supplyAsync(() -> craftyServer.apply(fen.orElse(""), level.orElse(1)), executor)
            .thenApply(s -> ResponseEntity.ok().body(s))
            .exceptionally(e -> ResponseEntity.badRequest().body(e.getMessage()))
            .join();

    }

}

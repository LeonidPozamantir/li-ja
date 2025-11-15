package leo.lija.app.game;

import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class HubMaster {

    private final HubMemo hubMemo;
    private final TaskExecutor executor;

    public CompletableFuture<Void> cleanup() {
        return CompletableFuture.runAsync(() -> hubMemo.all().forEach((id, hub) -> hub.withMembers(members -> {
            if (!members.isEmpty()) hubMemo.shake(id);
        })), executor);
    }

    public CompletableFuture<Integer> getNbMembers() {

        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] futures = hubActors().stream().map(Hub::getNbMembers).toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures)
            .thenApply(v -> Arrays.stream(futures).map(CompletableFuture::join)
                .mapToInt(Integer::intValue)
                .sum()
            );
    }

    public CompletableFuture<Void> nbPlayers(int nb) {
        return CompletableFuture.runAsync(() -> hubActors().forEach(actor -> actor.nbPlayers(nb)), executor);
    }

    private Map<String, Hub> hubs() {
        return hubMemo.all();
    }

    private Collection<Hub> hubActors() {
        return hubMemo.all().values();
    }
}

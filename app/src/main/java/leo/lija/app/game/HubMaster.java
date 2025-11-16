package leo.lija.app.game;

import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class HubMaster {

    private final HubMemo hubMemo;
    private final TaskExecutor executor;

    public CompletableFuture<Integer> getNbMembers() {

        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] futures = hubActors().stream().map(Hub::getNbMembers).toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures)
            .thenApply(v -> Arrays.stream(futures).map(CompletableFuture::join)
                .mapToInt(Integer::intValue)
                .sum()
            );
    }

    public void withHubs(Consumer<Map<String, Hub>> op) {
        op.accept(hubMemo.all());
    }

    public CompletableFuture<Void> nbPlayers(int nb) {
        return CompletableFuture.runAsync(() -> hubActors().forEach(actor -> actor.nbPlayers(nb)), executor);
    }

    private Collection<Hub> hubActors() {
        return hubMemo.all().values();
    }

    private Map<String, Hub> hubs() {
        return hubMemo.all();
    }

}

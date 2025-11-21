package leo.lija.app;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Hub {
    CompletableFuture<List<String>> getUsernames();

    CompletableFuture<Void> nbPlayers(int nb);

    CompletableFuture<Integer> getNbMembers();
}

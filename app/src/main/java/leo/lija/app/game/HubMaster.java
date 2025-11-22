package leo.lija.app.game;

import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class HubMaster {

    private final HubMemo hubMemo;
    private final TaskExecutor executor;

    public void withHubs(Consumer<Map<String, Hub>> op) {
        op.accept(hubMemo.all());
    }

    private Collection<Hub> hubActors() {
        return hubMemo.all().values();
    }

    private Map<String, Hub> hubs() {
        return hubMemo.all();
    }

}

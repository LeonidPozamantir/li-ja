package leo.lija.app;

import leo.lija.app.memo.AliveMemo;
import leo.lija.app.memo.HookMemo;
import leo.lija.app.memo.UsernameMemo;
import leo.lija.app.memo.WatcherMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Pinger {

    private final AliveMemo aliveMemo;
    private final UsernameMemo usernameMemo;
    private final WatcherMemo watcherMemo;

    public Map<String, Object> ping(
        Optional<String> username,
        Optional<String> playerKey,
        Optional<String> watcherKey,
        Optional<String> getNbWatchers
    ) {
        playerKey.ifPresent(aliveMemo::put);
        username.ifPresent(usernameMemo::put);
        watcherKey.ifPresent(watcherMemo::put);
        return flatten(Map.of(
            "nbp", Optional.of(aliveMemo.count()),
            "nbw", getNbWatchers.map(watcherMemo::count)
        ));
    }

    private<A, B> Map<A, B> flatten(Map<A, Optional<B>> map) {
        return map.entrySet().stream()
            .filter(e -> e.getValue().isPresent())
            .collect(Collectors.toMap(Map.Entry::getKey,e -> e.getValue().get()));
    }
}

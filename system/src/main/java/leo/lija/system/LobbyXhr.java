package leo.lija.system;

import leo.lija.system.config.LobbyConfigProperties;
import leo.lija.system.db.HookRepo;
import leo.lija.system.entities.Hook;
import leo.lija.system.memo.LobbyMemo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LobbyXhr {

    private final HookRepo hookRepo;
    private final LobbyMemo lobbyMemo;
    private final LobbyConfigProperties config;

    public Map<String, Object> sync(Optional<String> myHookId, boolean auth, int version) {
        int newVersion = versionWait(version);
        List<Hook> hooks = auth ? hookRepo.allOpen() : hookRepo.allOpenCasual();
        return Map.of(
            "state", newVersion,
            "pool", !hooks.isEmpty()
                ? Map.of("hooks", renderHooks(hooks, myHookId))
                : Map.of("message", "No game available right now, create one!"),
            "chat", "",
            "timeline", ""
        );
    }

    private Map<String, Map<String, Object>> renderHooks(List<Hook> hooks, Optional<String> myHookId) {
        return hooks.stream().collect(Collectors.toMap(Hook::getId, hook -> {
            Map<String, Object> res = hook.render();
            if (myHookId.isPresent() && myHookId.get().equals(hook.getOwnerId())) {
                res.putAll(Map.of("action", "cancel", "id", myHookId.get()));
            } else {
                res.putAll(Map.of("action", "join", "id", hook.getId()));
            }
            return res;
        }));
    }

    private int versionWait(int version) {
        return waitLoop(Math.max(1, config.poll().duration() / config.poll().sleep()), version);
    }

    @SneakyThrows
    private int waitLoop(Integer loop, Integer version) {
        if (loop == 0 || lobbyMemo.version() != version) {
            return lobbyMemo.version();
        }
        else {
            Thread.sleep(config.poll().sleep());
            return waitLoop(loop - 1, version);
        }
    }

}

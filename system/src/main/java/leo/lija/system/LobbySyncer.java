package leo.lija.system;

import leo.lija.system.config.LobbyConfigProperties;
import leo.lija.system.db.EntryRepo;
import leo.lija.system.db.GameRepo;
import leo.lija.system.db.HookRepo;
import leo.lija.system.dto.EntryDto;
import leo.lija.system.entities.Hook;
import leo.lija.system.entities.entry.Entry;
import leo.lija.system.memo.EntryMemo;
import leo.lija.system.memo.HookMemo;
import leo.lija.system.memo.LobbyMemo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LobbySyncer {

    private final HookRepo hookRepo;
    private final GameRepo gameRepo;
    private final EntryRepo entryRepo;
    private final LobbyMemo lobbyMemo;
    private final HookMemo hookMemo;
    private final EntryMemo entryMemo;
    private final LobbyConfigProperties config;

    public Map<String, Object> sync(Optional<String> myHookId, boolean auth, int version, int entryId) {
        myHookId.ifPresent(hookMemo::put);
        int newVersion = wait2(version, entryId);
        List<Hook> hooks = auth ? hookRepo.allOpen() : hookRepo.allOpenCasual();
        Supplier<Map<String, Object>> response = () -> stdResponse(newVersion, hooks, myHookId, entryId);
        return myHookId
            .map(hookId -> hookResponse(hookId, response))
            .orElse(response.get());
    }

    public Map<String, Object> hookResponse(String myHookId, Supplier<Map<String, Object>> response) {
        return hookRepo.findByOwnerId(myHookId)
            .map(hook -> Optional.ofNullable(hook.getGame())
                .map(ref -> gameRepo.game(ref))
                .map(game -> Map.of("redirect", (Object) game.fullIdOf(game.getCreatorColor())))
                .orElse(response.get())
            )
            .orElse(Map.of("redirect", ""));
    }

    public Map<String, Object> stdResponse(Integer version, List<Hook> hooks, Optional<String> myHookId, int entryId) {
        List<Entry> entries = entryId == 0
            ? entryRepo.recent(config.sync().maxEntries())
            : entryRepo.since(Math.max(entryMemo.id() - config.sync().maxEntries(), entryId));
        return Map.of(
            "state", version,
            "pool", !hooks.isEmpty()
                ? Map.of("hooks", renderHooks(hooks, myHookId))
                : Map.of("message", "No game available right now, create one!"),
            "chat", "",
            "timeline", !entries.isEmpty() ? renderTimeline(entries) : Map.of("id", entryId, "entries", List.of())
        );
    }

    private Map<String, Object> renderTimeline(List<Entry> entries) {
        return Map.of(
            "id", entries.get(0).getId(),
            "entries", entries.reversed().stream().map(entry -> new EntryDto(
                entry.getData().getId(),
                entry.getData().getPlayers(),
                entry.getData().getVariant(),
                entry.getData().getRated() ? "Rated" : "Casual",
                entry.getData().getClock().isEmpty() ? "Unlimited" : entry.getData().getClock().stream().map(String::valueOf).collect(Collectors.joining(" + "))
            )).toList()
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

    private int wait2(int version, int entryId) {
        return waitLoop(Math.max(1, config.sync().duration() / config.sync().sleep()), version, entryId);
    }

    @SneakyThrows
    private int waitLoop(int loop, int version, int entryId) {
        if (loop == 0
            || lobbyMemo.version() != version
            || entryMemo.id() != entryId) {
            return lobbyMemo.version();
        }
        else {
            Thread.sleep(config.sync().sleep());
            return waitLoop(loop - 1, version, entryId);
        }
    }

}

package leo.lija.app.lobby;

import leo.lija.app.db.EntryRepo;
import leo.lija.app.db.GameRepo;
import leo.lija.app.db.HookRepo;
import leo.lija.app.db.MessageRepo;
import leo.lija.app.entities.Entry;
import leo.lija.app.entities.Hook;
import leo.lija.app.entities.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class Preload {

    private final Fisherman fisherman;
    private final History history;
    private final HookRepo hookRepo;
    private final GameRepo gameRepo;
    private final MessageRepo messageRepo;
    private final EntryRepo entryRepo;

    public Map<String, Object> apply(boolean auth, boolean chat, Optional<String> myHookId) {
        Optional<Hook> myHook = myHookId.flatMap(hookRepo::findByOwnerId);
        myHook.ifPresent(fisherman::shake);
        List<Hook> hooks = auth ? hookRepo.allOpen() : hookRepo.allOpenCasual();
        Supplier<Map<String, Object>> response = () -> stdResponse(chat, hooks, myHook);
        return myHook
            .map(hook -> hookResponse(hook, response))
            .orElse(response.get());
    }

    private Map<String, Object> hookResponse(Hook myHook, Supplier<Map<String, Object>> response) {
        return Optional.ofNullable(myHook.getGame())
            .map(gameRepo::game)
            .map(game -> Map.of("redirect", (Object) game.fullIdOf(game.getCreatorColor())))
            .orElse(response.get());
    }

    private Map<String, Object> stdResponse(boolean chat, List<Hook> hooks, Optional<Hook> myHook) {
        List<Message> messages = chat ? messageRepo.recent() : List.of();
        List<Entry> entries = entryRepo.recent();
        return Map.of(
                "version", history.version(),
                "pool", renderHooks(hooks, myHook),
                "chat", messages.reversed().stream().map(Message::render).toList(),
                "timeline", entries.reversed().stream().map(Entry::render).toList()
        );
    }

    private List<Map<String, Object>> renderHooks(List<Hook> hooks, Optional<Hook> myHook) {
        return hooks.stream().map(h -> {
            Map<String, Object> res = h.render();
            if (myHook.isPresent() && myHook.get() == h) res.put("ownerId", h.getOwnerId());
            return res;
        }).toList();
    }

}

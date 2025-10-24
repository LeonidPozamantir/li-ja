package leo.lija.system;

import leo.lija.system.db.EntryRepo;
import leo.lija.system.db.GameRepo;
import leo.lija.system.db.HookRepo;
import leo.lija.system.db.MessageRepo;
import leo.lija.system.entities.Entry;
import leo.lija.system.entities.Hook;
import leo.lija.system.entities.Message;
import leo.lija.system.memo.HookMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class LobbyPreloader {

    private final HookRepo hookRepo;
    private final GameRepo gameRepo;
    private final MessageRepo messageRepo;
    private final EntryRepo entryRepo;
    private final HookMemo hookMemo;

    public Map<String, Object> apply(boolean auth, boolean chat, Optional<String> myHookId) {
        myHookId.ifPresent(hookMemo::put);
        List<Hook> hooks = auth ? hookRepo.allOpen() : hookRepo.allOpenCasual();
        Supplier<Map<String, Object>> response = () -> stdResponse(chat, hooks, myHookId);
        return myHookId
            .map(hookId -> hookResponse(hookId, response))
            .orElse(response.get());
    }

    public Map<String, Object> hookResponse(String myHookId, Supplier<Map<String, Object>> response) {
        return hookRepo.findByOwnerId(myHookId)
            .map(hook -> Optional.ofNullable(hook.getGame())
                .map(gameRepo::game)
                .map(game -> Map.of("redirect", (Object) game.fullIdOf(game.getCreatorColor())))
                .orElse(response.get())
            )
            .orElse(Map.of("redirect", ""));
    }

    public Map<String, Object> stdResponse(boolean chat, List<Hook> hooks, Optional<String> myHookId) {
        List<Message> messages = chat ? messageRepo.recent() : List.of();


        List<Entry> entries = entryRepo.recent();
        return Map.of(
                "pool", renderHooks(hooks, myHookId),
                "chat", messages.reversed().stream().map(Message::render).toList(),
                "timeline", entries.reversed().stream().map(Entry::render).toList()
        );
    }

    private List<Map<String, Object>> renderHooks(List<Hook> hooks, Optional<String> myHookId) {
        return hooks.stream().map(h -> {
            Map<String, Object> res = h.render();
            if (myHookId.isPresent() && myHookId.get().equals(h.getOwnerId()))
                res.putAll(Map.of("action", "cancel", "id", myHookId.get()));
            return res;
        }).toList();
    }

}

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
        List<Hook> hooks = auth ? hookRepo.allOpen() : hookRepo.allOpenCasual();
        Supplier<Map<String, Object>> std = () -> stdResponse(chat, hooks, myHookId);
        return myHookId
            .map(id -> hookResponse(hookRepo.findByOwnerId(id), std))
            .orElse(std.get());
    }

    private Map<String, Object> hookResponse(Optional<Hook> myHook, Supplier<Map<String, Object>> std) {
        return myHook
            .map(h -> h.gameId()
                .map(gameId -> gameRepo.gameOption(gameId)
                    .map(g -> redirect(g.fullIdOf(g.getCreatorColor())))
                    .orElse(redirect(null)))
                .orElseGet(() -> {
                    fisherman.shake(h);
                    return std.get();
                }))
            .orElse(redirect(null));
    }

    private Map<String, Object> stdResponse(boolean chat, List<Hook> hooks, Optional<String> myHookId) {
        List<Message> messages = chat ? messageRepo.recent() : List.of();
        List<Entry> entries = entryRepo.recent();
        return Map.of(
                "version", history.version(),
                "pool", renderHooks(hooks, myHookId),
                "chat", messages.reversed().stream().map(Message::render).toList(),
                "timeline", entries.reversed().stream().map(Entry::render).toList()
        );
    }

    private List<Map<String, Object>> renderHooks(List<Hook> hooks, Optional<String> myHookId) {
        return hooks.stream().map(h -> {
            Map<String, Object> res = h.render();
            if (myHookId.isPresent() && myHookId.get().equals(h.getOwnerId())) res.put("ownerId", h.getOwnerId());
            return res;
        }).toList();
    }

    private Map<String, Object> redirect(String url) {
        return Map.of("redirect", "/" + Optional.ofNullable(url).orElse(""));
    }
}

package leo.lija.system;

import leo.lija.system.config.LobbyConfigProperties;
import leo.lija.system.db.EntryRepo;
import leo.lija.system.db.GameRepo;
import leo.lija.system.db.HookRepo;
import leo.lija.system.db.MessageRepo;
import leo.lija.system.dto.EntryDto;
import leo.lija.system.entities.Hook;
import leo.lija.system.entities.Message;
import leo.lija.system.entities.entry.Entry;
import leo.lija.system.memo.HookMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LobbyPreloader {

    private final HookRepo hookRepo;
    private final GameRepo gameRepo;
    private final MessageRepo messageRepo;
    private final EntryRepo entryRepo;
    private final HookMemo hookMemo;
    private final LobbyConfigProperties config;

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
        List<Message> messages = chat ? messageRepo.recent(config.sync().message().max()) : List.of();


        List<Entry> entries = entryRepo.recent(config.sync().entry().max());
        return Map.of(
                "pool", !hooks.isEmpty()
                        ? Map.of("hooks", renderHooks(hooks, myHookId))
                        : Map.of("message", "No game available right now, create one!"),
                "chat", !messages.isEmpty() ? renderMessages(messages) : List.of(),
                "timeline", !entries.isEmpty() ? renderEntries(entries) : List.of()
        );
    }

    private List<Map<String, String>> renderMessages(List<Message> messages) {
        return messages.reversed().stream().map(message -> Map.of(
                "u", message.getUsername(),
                "txt", message.getMessage()
            )).toList();
    }

    private List<EntryDto> renderEntries(List<Entry> entries) {
        return entries.reversed().stream().map(entry -> new EntryDto(
                entry.getData().getId(),
                entry.getData().getPlayers(),
                entry.getData().getVariant(),
                entry.getData().getRated() ? "Rated" : "Casual",
                Optional.ofNullable(entry.getData().getClock()).orElse("Unlimited")
            )).toList();
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

}

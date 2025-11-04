package leo.lija.app.lobby;

import leo.lija.app.config.SocketIOService;
import leo.lija.app.db.MessageRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Entry;
import leo.lija.app.entities.Hook;
import leo.lija.app.entities.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Hub {

    private final SocketIOService socketService;
    private final MessageRepo messageRepo;
    private final History history;

    private final Map<String, Member> members = new ConcurrentHashMap<>();

    public List<String> getHooks() {
        return members.values().stream()
            .filter(m -> m.hookOwnerId().isPresent())
            .map(m -> m.hookOwnerId().get())
            .toList();
    }

    public Set<String> getUsernames() {
        return members.values().stream()
            .filter(m -> m.username().isPresent())
            .map(m -> m.username().get())
            .collect(Collectors.toSet());
    }

    public void join(String uid, Integer version, Optional<String> username, Optional<String> hookOwnerId) {
        socketService.addToRoom("lobby", uid);
        history.since(version).forEach(m -> socketService.sendMessage("lobby", m));
        members.put(uid, new Member(uid, username, hookOwnerId));
    }

    public void talk(String txt, String u) {
        Message message = messageRepo.add(txt, u);
        notifyVersion("talk", Map.of(
            "txt", message.getText(),
            "u", message.getUsername()
        ));
    }

    public void addEntry(Entry entry) {
        notifyVersion("entry", entry.render());
    }

    public void addHook(Hook hook) {
        Map<String, Object> data = new HashMap<>(Map.of(
                "id", hook.getId(),
                "username", hook.getUsername(),
                "elo", hook.getElo(),
                "mode", hook.realMode().toString(),
                "variant", hook.realVariant().toString(),
                "color", hook.getColor(),
                "clock", hook.clockOrUnlimited(),
                "engine", hook.getEngine()
        ));
        data.put("emin", hook.eloMin().orElse(null));
        data.put("emax", hook.eloMax().orElse(null));
        notifyVersion("hook_add", data);
    }

    public void removeHook(Hook hook) {
        notifyVersion("hook_remove", hook.getId());
    }

    public void biteHook(Hook hook, DbGame game) {
        members.values().stream().filter(m -> m.ownsHook(hook))
                .forEach(m -> notifyMember("redirect", game.fullIdOf(game.getCreatorColor()), m));
    }

    public void nbPlayers() {
        notifyAll("nbp", members.size());
    }

    public void quit(String uid) {
        members.remove(uid);
    }

    public void notifyMember(String t, Object data, Member member) {
        Map<String, Object> msg = Map.of(
                "t", t,
                "d", data
        );
        socketService.sendMessageToClient(member.uid(), msg);
    }

    public void notifyAll(String t, Object data) {
        Map<String, Object> msg = makeMessage(t, data);
        socketService.sendMessage("lobby", msg);
    }

    private void notifyVersion(String t, Object data) {
        Map<String, Object> vmsg = history.add(makeMessage(t, data));
        socketService.sendMessage("lobby", vmsg);
    }

    private Map<String, Object> makeMessage(String t, Object data) {
        return Map.of(
                "t", t,
                "d", data
        );
    }
}

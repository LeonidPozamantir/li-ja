package leo.lija.app.lobby;

import leo.lija.app.config.SocketIOService;
import leo.lija.app.db.MessageRepo;
import leo.lija.app.entities.Entry;
import leo.lija.app.entities.Hook;
import leo.lija.app.entities.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class Hub {

    private final SocketIOService socketService;
    private final MessageRepo messageRepo;
    private final History history;

    private final Set<String> members = ConcurrentHashMap.newKeySet();

    public void join(String uid, Integer version) {
        socketService.addToRoom("lobby", uid);
        List<Map<String, Object>> messages = history.since(version);
        messages.forEach(m -> socketService.sendMessage("lobby", m));
        members.add(uid);
    }

    public void talk(String txt, String u) {
        Message message = messageRepo.add(txt, u);
        notifyAll("talk", Map.of(
            "txt", message.getText(),
            "u", message.getUsername()
        ));
    }

    public void addEntry(Entry entry) {
        notifyAll("entry", entry.render());
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
                "action", "join",
                "engine", hook.getEngine()
        ));
        data.put("emin", hook.eloMin().orElse(null));
        data.put("emax", hook.eloMax().orElse(null));
        notifyAll("hook_add", data);
    }

    public void removeHook(Hook hook) {
        notifyAll("hook_remove", hook.getId());
    }

    public void quit(String uid) {
        members.remove(uid);
    }

    public void notifyAll(String t, Object data) {
        Map<String, Object> msg = Map.of(
            "t", t,
            "d", data
        );
        Map<String, Object> vmsg = history.add(msg);
        socketService.sendMessage("lobby", vmsg);
    }
}

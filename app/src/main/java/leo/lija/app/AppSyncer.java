package leo.lija.app;

import leo.lija.chess.Color;
import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.Room;
import leo.lija.app.entities.event.Event;
import leo.lija.app.entities.event.MessageEvent;
import leo.lija.app.entities.event.RedirectEvent;
import leo.lija.app.memo.AliveMemo;
import leo.lija.app.memo.VersionMemo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppSyncer {

    private final GameRepo gameRepo;
    private final VersionMemo versionMemo;
    private final AliveMemo aliveMemo;

    @Value("${sync.duration}")
    int duration;
    @Value("${sync.sleep}")
    int sleep;

    public Optional<Map<String, Object>> sync(String gameId, String colorString, Integer version, Optional<String> fullId) {
        return Color.apply(colorString).flatMap(color -> {
            versionWait(gameId, color, version);
            return gameRepo.povOption(gameId, color)
                .map(pov -> {
                    versionMemo.put(pov.game());
                    return pov.player().eventStack().eventsSince(version).map(events -> {
                            Map<String, Object> res = new HashMap<>();
                            res.putAll(Map.of(
                                "v", pov.player().eventStack().lastVersion(),
                                "e", renderEvents(events, pov.isPlayerFullId(fullId)),
                                "p", pov.game().player().getColor().name(),
                                "t", pov.game().getTurns(),
                                "oa", aliveMemo.activity(pov.game(), color.getOpposite())
                            ));
                            res.put("c", pov.game().getClock().map(clock -> clock.remainingTimes().entrySet().stream()
                                .collect(Collectors.toMap(e -> e.getKey().getName(), e -> e.getValue()))).orElse(null));
                            res.entrySet().removeIf(e -> e.getValue() == null);
                            return res;
                        }
                    ).orElse(Map.of("reload", true));
                });
        });
    }

    private List<Map<String, Object>> renderEvents(List<Event> events, boolean isPrivate) {
        if (isPrivate) {
            return events.stream().map(e -> {
                if (e instanceof MessageEvent messageEvent) {
                    String author = messageEvent.getAuthor();
                    String message = messageEvent.getMessage();
                    return renderMessage(author, message);
                } else return e.export();
            }).toList();
        } else {
            return events.stream()
                .filter(e -> !(e instanceof MessageEvent || e instanceof RedirectEvent))
                .map(Event::export)
                .toList();
        }
    }

    private Map<String, Object> renderMessage(String author, String message) {
        return Map.of(
            "type", "message",
            "message", new Room.RoomMessage(author, message)
        );
    }

    private void versionWait(String gameId, Color color, Integer version) {
        wait(Math.max(1, duration / sleep), gameId, color, version);
    }

    @SneakyThrows
    private void wait(Integer loop, String gameId, Color color, Integer version) {
        if (loop == 0 || !versionMemo.get(gameId, color).equals(version)) {
            // do nothing
        }
        else {
            Thread.sleep(sleep);
            wait(loop - 1, gameId, color, version);
        }
    }

    private final Map<String, Object> failMap = Map.of("reload", true);
}

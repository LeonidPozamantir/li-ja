package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.event.Event;
import leo.lija.system.entities.event.MessageEvent;
import leo.lija.system.entities.event.RedirectEvent;
import leo.lija.system.memo.VersionMemo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Syncer {

    private final GameRepo repo;
    private final VersionMemo versionMemo;

    @Value("${sync.duration}")
    int duration;
    @Value("${sync.sleep}")
    int sleep;

    public Map<String, Object> sync(String gameId, String colorString, Integer version, Optional<String> fullId) {
        try {
            return Color.apply(colorString)
                .map(color -> {
                    versionWait(gameId, color, version);
                    return repo.player(gameId, color);
                })
                .map(gameAndPlayer -> {
                    DbGame game = gameAndPlayer.getFirst();
                    DbPlayer player = gameAndPlayer.getSecond();
                    boolean isPrivate = fullId.map(fid -> game.isPlayerFullId(player, fid)).orElse(false);
                    versionMemo.put(game);
                    return player.eventStack().eventsSince(version).map(events ->
                        Map.of(
                        "v", player.eventStack().lastVersion(),
                        "e", renderEvents(events, isPrivate),
                        "p", game.player().getColor().name(),
                        "t", game.getTurns()
                    )).orElse(failMap);
                }).orElse(failMap);
        } catch (Exception e) {
            return failMap;
        }
    }

    private List<Map<String, Object>> renderEvents(List<Event> events, boolean isPrivate) {
        if (isPrivate) {
            return events.stream().map(e -> {
                if (e instanceof MessageEvent) {
                    String author = ((MessageEvent) e).getAuthor();
                    String message = ((MessageEvent) e).getMessage();
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
            "type", "html",
            "author", author,
            "message", message
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
            this.wait(loop - 1);
        }
    }

    private final Map<String, Object> failMap = Map.of("failMap", true);
}

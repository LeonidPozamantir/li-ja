package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.chess.utils.Pair;
import leo.lija.system.db.GameRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.event.Event;
import leo.lija.system.entities.event.MessageEvent;
import leo.lija.system.entities.event.RedirectEvent;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.VersionMemo;
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
public class Syncer {

    private final GameRepo repo;
    private final VersionMemo versionMemo;
    private final AliveMemo aliveMemo;

    @Value("${sync.duration}")
    int duration;
    @Value("${sync.sleep}")
    int sleep;

    public Map<String, Object> sync(String gameId, String colorString, Integer version, Optional<String> fullId) {
        try {
            return Color.apply(colorString)
                .flatMap(color -> {
                    versionWait(gameId, color, version);
                    Pair<DbGame, DbPlayer> gameAndPlayer = repo.player(gameId, color);
                    DbGame game = gameAndPlayer.getFirst();
                    DbPlayer player = gameAndPlayer.getSecond();
                    boolean isPrivate = fullId.map(fid -> game.isPlayerFullId(player, fid)).orElse(false);
                    versionMemo.put(game);
                    return player.eventStack().eventsSince(version).map(events -> {
                            Map<String, Object> res = new HashMap<>();
                            res.putAll(Map.of(
                                "v", player.eventStack().lastVersion(),
                                "e", renderEvents(events, isPrivate),
                                "p", game.player().getColor().name(),
                                "t", game.getTurns(),
                                "oa", aliveMemo.activity(game, color.getOpposite())
                            ));
                            res.put("c", game.getClock().map(clock -> clock.remainingTimes().entrySet().stream()
                                .collect(Collectors.toMap(e -> e.getKey().getName(), e -> e.getValue()))).orElse(null));
                            res.entrySet().removeIf(e -> e.getValue() == null);
                            return res;
                        }
                    );
                }).orElse(failMap);
        } catch (Exception e) {
            return failMap;
        }
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
            wait(loop - 1, gameId, color, version);
        }
    }

    private final Map<String, Object> failMap = Map.of("reload", true);
}

package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.event.Event;
import leo.lija.system.memo.VersionMemo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class Syncer {

    private final GameRepo repo;
    private final VersionMemo versionMemo;

    @Value("${sync.duration}")
    int duration;
    @Value("${sync.sleep}")
    int sleep;

    public Map<String, Object> sync(String gameId, String colorString, Integer version, String fullId) {
        try {
            return Color.apply(colorString)
                .map(color -> {
                    versionWait(gameId, color, version);
                    return repo.player(gameId, color);
                })
                .map(gameAndPlayer -> {
                    DbGame game = gameAndPlayer.getFirst();
                    DbPlayer player = gameAndPlayer.getSecond();
                    versionMemo.put(game);
                    return player.eventStack().eventsSince(version).map(events ->
                        Map.of(
                        "v", player.eventStack().lastVersion(),
                        "e", events.stream().map(Event::export).toList(),
                        "p", game.player().getColor().name(),
                        "t", game.getTurns()
                    )).orElse(failMap);
                }).orElse(failMap);
        } catch (Exception e) {
            return failMap;
        }
    }

    private void versionWait(String gameId, Color color, Integer version) {
        wait(Math.max(1, duration / sleep), gameId, color, version);
    }

    @SneakyThrows
    private void wait(Integer loop, String gameId, Color color, Integer version) {
        if (loop == 0 || versionMemo.get(gameId, color) != version) {}
        else {
            Thread.sleep(sleep);
            wait(loop - 1);
        }
    }

    private final Map<String, Object> failMap = Map.of("failMap", true);
}

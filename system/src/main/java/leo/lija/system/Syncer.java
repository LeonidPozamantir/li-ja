package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.event.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Syncer {

    private final GameRepo repo;

    private final Map<String, Object> reload = Map.of("reload", true);

    public Map<String, Object> sync(String id, String colorString, Integer version, String fullId) {
        try {
            return Color.apply(colorString)
                .map(color -> repo.player(id, color))
                .map(gameAndPlayer -> {
                    DbGame g = gameAndPlayer.getFirst();
                    DbPlayer p = gameAndPlayer.getSecond();
                    return p.eventStack().eventsSince(version).map(events ->
                        Map.of(
                        "v", p.eventStack().lastVersion(),
                        "e", events.stream().map(Event::export).toList(),
                        "p", g.player().getColor().name(),
                        "t", g.getTurns()
                    )).orElse(reload);
                }).orElse(reload);
        } catch (Exception e) {
            return reload;
        }
    }

    public List<Event> eventsSince(DbPlayer player, Integer version) {
        return player.eventStack().eventsSince(version).orElse(List.of());
    }
}

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

    public Map<String, Object> sync(String id, String colorString, Integer version, String fullId) {
        try {
            return Color.apply(colorString)
                .map(color -> repo.player(id, color))
                .map(gameAndPlayer -> {
                    DbGame g = gameAndPlayer.getFirst();
                    DbPlayer p = gameAndPlayer.getSecond();
                    return Map.of(
                        "v", p.eventStack().version(),
                        "p", g.player(),
                        "t", g.getTurns()
                    );
                }).orElse(Map.of("reload", true));
        } catch (Exception e) {
            return Map.of("reload", true);
        }
    }

    public List<Event> eventsSince(DbPlayer player, Integer version) {
        return player.eventStack().eventsSince(version).orElse(List.of());
    }
}

package leo.lija.app;

import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Pov;
import leo.lija.app.entities.Progress;
import leo.lija.app.entities.event.Event;
import leo.lija.app.entities.event.ReloadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Takeback {

    private final GameRepo gameRepo;
    private final Messenger messenger;

    public List<Event> apply(DbGame game) {
        return save(game.rewind());
    }

    public List<Event> _double(DbGame game) {
        Progress p1 = game.rewind();
        Progress p = p1.game().rewind();
        Progress p2 = p1.withGame(p.game());
        return save(p2);
    }

    private List<Event> save(Progress p1) {
        messenger.systemMessage(p1.game(), "Takeback proposition accepted");
        p1.add(new ReloadEvent());
        gameRepo.save(p1);
        return p1.events();
    }
}

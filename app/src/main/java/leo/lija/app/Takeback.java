package leo.lija.app;

import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.event.Event;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Takeback {

    public List<Event> perform(DbGame game) {
        return List.of();
    }
}

package leo.lija.app.entities;

import leo.lija.app.entities.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public record Progress(DbGame game, List<Event> events) {

    public Progress(DbGame game) {
        this(game, new ArrayList<>());
    }

    public void add(Event event) {
        events.add(event);
    }

    public void addAll(List<Event> es) {
        events.addAll(es);
    }

    public Progress map(UnaryOperator<DbGame> f) {
        return new Progress(f.apply(game), events);
    }

    public Progress flatMap(Function<DbGame, Progress> f) {
        Progress ev = f.apply(game);
        Progress res = new Progress(ev.game, events);
        res.addAll(ev.events);
        return res;
    }
}

package leo.lija.app.entities;

import leo.lija.app.entities.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public record Evented(DbGame game, List<Event> events) {

    public Evented(DbGame game) {
        this(game, new ArrayList<>());
    }

    public void add(Event event) {
        events.add(event);
    }

    public void addAll(List<Event> es) {
        events.addAll(es);
    }

    public Evented map(UnaryOperator<DbGame> f) {
        return new Evented(f.apply(game), events);
    }

    public Evented flatMap(Function<DbGame, Evented> f) {
        Evented ev = f.apply(game);
        Evented res = new Evented(ev.game, events);
        res.addAll(ev.events);
        return res;
    }
}

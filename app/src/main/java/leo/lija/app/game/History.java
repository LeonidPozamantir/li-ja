package leo.lija.app.game;

import com.google.common.cache.Cache;
import leo.lija.app.entities.event.Event;
import leo.lija.app.memo.Builder;
import leo.lija.chess.Color;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class History {

    private final Cache<@NonNull Integer, @NonNull VersionedEvent> events;

    public History(int timeout) {
        this.events = Builder.expiry(timeout);
    }

    public record VersionedEvent(Map<String, Object> js, Optional<Color> only, boolean own) {
        public boolean visible(Color color, boolean owner) {
            if (own && !owner) return false;
            return only.map(c -> c == color).orElse(true);
        }

        public boolean visible(Member member) {
            return visible(member.color(), member.owner);
        }
    }

    private int privateVersion = 0;

    public int version() {
        return privateVersion;
    }

    public List<VersionedEvent> since(Integer v) {
        return IntStream.rangeClosed(v + 1, version())
            .mapToObj(this::event)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    private Optional<VersionedEvent> event(Integer v) {
        return Optional.ofNullable(events.getIfPresent(v));
    }

    public VersionedEvent add(Event event) {
        privateVersion++;
        VersionedEvent vevent = new VersionedEvent(
            Map.of(
                "v", privateVersion,
                "t", event.typ(),
                "d", event.data()
            ),
            event.only(),
            event.owner()
        );
        events.put(privateVersion, vevent);
        return vevent;
    }
}

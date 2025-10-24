package leo.lija.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import leo.lija.chess.Clock;
import leo.lija.chess.utils.Pair;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity
@Table(name = "lobby_entry")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Data
public class Entry {

    @Id
    private String gameId;

    @NotNull
    @Column(nullable = false)
    private String whiteName;

    @NotNull
    @Column(nullable = false)
    private String blackName;

    private String whiteId;
    private String blackId;

    @NotNull
    @Column(nullable = false)
    private String variant;

    @NotNull
    @Column(nullable = false)
    private Boolean rated;

    private String clock;

    public List<Pair<String, String>> players() {
        return List.of(
            Pair.of(whiteName, whiteId),
            Pair.of(blackName, blackId)
        );
    }

    public Map<String, Object> render() {
        return Map.of(
            "gameId", gameId,
            "players", players().stream().map(p -> List.of(p.getSecond(), p.getFirst())),
            "veriant", variant,
            "rated", rated ? "Rated" : "Casual",
            "clock", Optional.ofNullable(clock).orElse("Unlimited")
        );
    }

    public static Optional<Entry> apply(DbGame game, String encodedData) {
        String[] data = encodedData.split("\\$");
        if (data.length != 4) return Optional.empty();
        String wu = data[0];
        String wue = data[1];
        String bu = data[2];
        String bue = data[3];
        return Optional.of(new Entry(
            game.getId(),
            wue,
            bue,
            wu.isEmpty() ? null : wu,
            bu.isEmpty() ? null : bu,
            game.getVariant().name(),
            game.isRated(),
            game.getClock().map(Clock::show).orElse(null)
        ));
    }
}

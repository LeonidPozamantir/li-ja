package leo.lija.system.entities.entry;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import leo.lija.chess.Clock;
import leo.lija.system.entities.DbGame;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Entity
@Table(name = "lobby_entry")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Data
public class Entry {

    @Id
    @NotNull
    @Column(name = "_id", nullable = false)
    private Integer id;

    @Embedded
    private EntryGame data;

    public static Optional<Function<Integer, Entry>> build(DbGame game, String encodedData) {
        String[] data = encodedData.split("\\$");
        if (data.length != 4) return Optional.empty();
        String wu = data[0];
        String wue = data[1];
        String bu = data[2];
        String bue = data[3];
        return Optional.of((id) -> new Entry(
            id,
            new EntryGame(
                game.getId(),
                List.of(
                    new EntryPlayer(
                        wu.isEmpty() ? null : wu,
                        wue
                    ),
                    new EntryPlayer(
                        bu.isEmpty() ? null : bu,
                        bue
                    )
                ),
                game.getVariant().name(),
                game.isRated(),
                game.getClock().map(Clock::show).orElse(null)
            )
        ));
    }
}

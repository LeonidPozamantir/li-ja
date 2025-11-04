package leo.lija.system.entities.entry;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Data
public class EntryGame {

    @NotNull
    @Column(nullable = false)
    private String id;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<EntryPlayer> players;

    @NotNull
    @Column(nullable = false)
    private String variant;

    @NotNull
    @Column(nullable = false)
    private Boolean rated;

    private String clock;
}

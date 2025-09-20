package leo.lija.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Hook {

    @Id
    private String id;

    @NotNull
    @Column(nullable = false)
    private String ownerId;

    @Column(nullable = false)
    private int variant;

    private Integer time;
    private Integer increment;

    @Column(nullable = false)
    private int mode;

    @NotNull
    @Column(nullable = false)
    private String color;

    @NotNull
    @Column(nullable = false)
    private String username;

    private Integer elo;

    @Column(nullable = false)
    private boolean match;

    private String eloRange;

    @Column(nullable = false)
    private boolean engine;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Variant realVariant() {
        return Variant.apply(variant).orElse(Variant.STANDARD);
    }

    public Mode realMode() {
        return Mode.apply(mode).orElse(Mode.CASUAL);
    }

    public Optional<String> eloMin() {
        return Optional.ofNullable(eloRange).map(er -> er.contains("-") ? er.substring(0, er.indexOf("-")) : er);
    }

    public Optional<String> eloMax() {
        return Optional.ofNullable(eloRange).map(er -> er.contains("-") ? er.substring(er.indexOf("-") + 1) : "");
    }

    public Map<String, Object> render() {
        HashMap<String, Object> res = new HashMap<>(Map.of(
            "username", username,
            "elo", elo,
            "variant", realVariant().toString(),
            "mode", realMode().toString(),
            "color", color,
            "clock", time != null && increment != null ? renderClock(time, increment) : "Unlimited",
            "emin", eloMin(),
            "emax", eloMax()
        ));
        if (engine) res.put("engine", true);
        return res;
    }

    private String renderClock(int time, int inc) {
        return "%d + %d".formatted(time, inc);
    }
}

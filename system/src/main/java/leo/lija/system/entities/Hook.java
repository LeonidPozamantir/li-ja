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

    @NotNull
    @Id
    private String id;

    @NotNull
    @Column(nullable = false)
    private String ownerId;

    @NotNull
    @Column(nullable = false)
    private Integer variant;

    private Integer time;
    private Integer increment;

    @NotNull
    @Column(nullable = false)
    private Integer mode;

    @NotNull
    @Column(nullable = false)
    private String color;

    @NotNull
    @Column(nullable = false)
    private String username;

    private Integer elo;

    @NotNull
    @Column(nullable = false)
    private Boolean match;

    private String eloRange;

    @NotNull
    @Column(nullable = false)
    private Boolean engine;

    private String game;

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

package leo.lija.app.entities;

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

    @NotNull
    @Column(nullable = false)
    private Boolean hasClock;

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

    public Optional<Integer> eloMin() {
        return Optional.ofNullable(eloRange).flatMap(er -> parseIntOptional(er.contains("-") ? er.substring(0, er.indexOf("-")) : er));
    }

    public Optional<Integer> eloMax() {
        return Optional.ofNullable(eloRange).flatMap(er -> parseIntOptional(er.contains("-") ? er.substring(er.indexOf("-") + 1) : ""));
    }

    private static Optional<Integer> parseIntOptional(String str) {
        try {
            return Optional.of(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Map<String, Object> render() {
        HashMap<String, Object> res = new HashMap<>(Map.of(
            "id", id,
            "username", username,
            "elo", elo,
            "variant", realVariant().toString(),
            "mode", realMode().toString(),
            "color", color,
            "clock", clockOrUnlimited(),
            "emin", eloMin(),
            "emax", eloMax()
        ));
        if (engine) res.put("engine", true);
        return res;
    }

    public String clockOrUnlimited() {
        return time != null && hasClock && increment != null ? renderClock(time, increment) : "Unlimited";
    }

    private String renderClock(int time, int inc) {
        return "%d + %d".formatted(time, inc);
    }
}

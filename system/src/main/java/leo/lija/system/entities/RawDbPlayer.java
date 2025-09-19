package leo.lija.system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import leo.lija.chess.Color;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Data
public class RawDbPlayer {

    @NotNull
    @Column(nullable = false)
    private String id;

    @NotNull
    @Column(nullable = false)
    private String color;

    @NotNull
    @Column(nullable = false)
    private String ps;

    private Integer aiLevel;
    private Boolean isWinner;
    private String evts;
    private Integer elo;
    private Integer lastDrawOffer;

    public RawDbPlayer copy() {
        return new RawDbPlayer(id, color, ps, aiLevel, isWinner, evts, elo, lastDrawOffer);
    }

    public Optional<DbPlayer> decode() {
        return Color.apply(color).map(trueColor -> new DbPlayer(
            id, trueColor, ps, aiLevel, isWinner, evts, elo, lastDrawOffer
        ));
    }

    public static RawDbPlayer encode(DbPlayer dbPlayer) {
        return new RawDbPlayer(
            dbPlayer.getId(),
            dbPlayer.getColor().getName(),
            dbPlayer.getPs(),
            dbPlayer.getAiLevel(),
            dbPlayer.getIsWinner(),
            dbPlayer.getEvts(),
            dbPlayer.getElo(),
            dbPlayer.getLastDrawOffer()
        );
    }
}

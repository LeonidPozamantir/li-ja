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
    private String color;           // c

    @NotNull
    @Column(nullable = false)
    private String ps;

    private Integer aiLevel;
    private Boolean isWinner;       // w
    private String evts;
    private Integer elo;
    private Boolean isOfferingDraw;
    private Integer lastDrawOffer;
    private String userId;
    private Integer eloDiff;

    public RawDbPlayer copy() {
        return new RawDbPlayer(id, color, ps, aiLevel, isWinner, evts, elo, isOfferingDraw, lastDrawOffer, userId, eloDiff);
    }

    public Optional<DbPlayer> decode() {
        return Color.apply(color).map(trueColor -> new DbPlayer(
            id,
            trueColor,
            ps,
            Optional.ofNullable(aiLevel),
            Optional.ofNullable(isWinner),
            evts,
            Optional.ofNullable(elo),
            isOfferingDraw,
            Optional.ofNullable(lastDrawOffer),
            Optional.ofNullable(userId)
        ));
    }

    public static RawDbPlayer encode(DbPlayer dbPlayer) {
        return new RawDbPlayer(
            dbPlayer.getId(),
            dbPlayer.getColor().getName(),
            dbPlayer.getPs(),
            dbPlayer.getAiLevel().orElse(null),
            dbPlayer.getIsWinner().orElse(null),
            dbPlayer.getEvts(),
            dbPlayer.getElo().orElse(null),
            dbPlayer.getIsOfferingDraw(),
            dbPlayer.getLastDrawOffer().orElse(null),
            dbPlayer.getUserId().orElse(null),
            null
        );
    }
}

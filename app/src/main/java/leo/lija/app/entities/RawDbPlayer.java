package leo.lija.app.entities;

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
    private Integer elo;
    private Boolean isOfferingDraw;
    private Integer lastDrawOffer;
    private String userId;
    private Integer eloDiff;
    private Integer blurs;

    public RawDbPlayer copy() {
        return new RawDbPlayer(id, color, ps, aiLevel, isWinner, elo, isOfferingDraw, lastDrawOffer, userId, eloDiff, blurs);
    }

    public Optional<DbPlayer> decode() {
        return Color.apply(color).map(trueColor -> new DbPlayer(
            id,
            trueColor,
            ps,
            Optional.ofNullable(aiLevel),
            Optional.ofNullable(isWinner),
            Optional.ofNullable(elo),
            isOfferingDraw,
            Optional.ofNullable(lastDrawOffer),
            Optional.ofNullable(userId),
            Optional.ofNullable(blurs).orElse(0)
        ));
    }

    public static RawDbPlayer encode(DbPlayer dbPlayer) {
        return new RawDbPlayer(
            dbPlayer.getId(),
            dbPlayer.getColor().getName(),
            dbPlayer.getPs(),
            dbPlayer.getAiLevel().orElse(null),
            dbPlayer.getIsWinner().orElse(null),
            dbPlayer.getElo().orElse(null),
            dbPlayer.getIsOfferingDraw(),
            dbPlayer.getLastDrawOffer().orElse(null),
            dbPlayer.getUserId().orElse(null),
            null,
            dbPlayer.getBlurs() == 0 ? null : dbPlayer.getBlurs()
        );
    }
}

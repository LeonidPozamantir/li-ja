package leo.lija.app.entities;

import jakarta.persistence.Embeddable;
import leo.lija.chess.Color;
import leo.lija.chess.Piece;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
@Data
public class DbPlayer {

    private String id;
    private Color color;
    private String ps;
    private Optional<Integer> aiLevel;
    private Optional<Boolean> isWinner;
    private Optional<Integer> elo;
    private Boolean isOfferingDraw;
    private Optional<Integer> lastDrawOffer;
    private Optional<String> userId;
    private Integer blurs;

    public DbPlayer copy() {
        return new DbPlayer(id, color, ps, aiLevel, isWinner, elo, isOfferingDraw, lastDrawOffer, userId, blurs);
    }

    public String encodePieces(Map<Pos, Piece> pieces, io.vavr.collection.List<Pair<Pos, Piece>> deads) {
        return Stream.concat(
            pieces.entrySet().stream()
                .filter(e -> e.getValue().is(color))
                .map(e -> String.valueOf(e.getKey().getPiotr()) + e.getValue().role().fen),
            deads.toJavaStream()
                .filter(p -> p.getSecond().is(color))
                .map(p -> String.valueOf(p.getFirst().getPiotr()) + Character.toUpperCase(p.getSecond().role().fen))
        ).collect(Collectors.joining(" "));
    }

    public boolean isAi() {
        return aiLevel.isPresent();
    }

    public boolean isHuman() {
        return !isAi();
    }

    public boolean wins() {
        return isWinner.orElse(false);
    }

    public DbPlayer finish(boolean winner) {
        DbPlayer res = copy();
        res.isWinner = winner ? Optional.of(true) : Optional.empty();
        return res;
    }

    public DbPlayer offerDraw(int turn) {
        DbPlayer res = copy();
        res.isOfferingDraw = true;
        res.lastDrawOffer = Optional.of(turn);
        return res;
    }

    public DbPlayer removeDrawOffer() {
        DbPlayer res = copy();
        res.isOfferingDraw = false;
        return res;
    }

}

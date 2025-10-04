package leo.lija.system.entities;


import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import leo.lija.chess.Clock;
import leo.lija.chess.Color;
import leo.lija.chess.Pos;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "game")
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Data
public class RawDbGame {

    @Id
    private String id;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<RawDbPlayer> players;

    @NotNull
    @Column(nullable = false)
    private String pgn;
    private int status;
    private int turns;
    @Embedded
    private RawDbClock clock;
    private String lastMove;
    @Column(name = "pos_check")
    private String check;
    private String creatorColor;        // cc
    private String positionHashes;
    private String castles;
    private boolean isRated;
    private int variant;                // v
    private String winnerId;

    public RawDbGame(String id, List<RawDbPlayer> players, String pgn, int status, int turns, RawDbClock clock, String lastMove, String check, String creatorColor, String positionHashes, String castles, boolean isRated, int variant, String winnerId) {
        this.id = id;
        this.players = players;
        this.pgn = pgn;
        this.status = status;
        this.turns = turns;
        this.clock = clock;
        this.lastMove = lastMove;
        this.check = check;
        this.creatorColor = creatorColor;
        this.positionHashes = positionHashes;
        this.castles = castles;
        this.isRated = isRated;
        this.variant = variant;
        this.winnerId = winnerId;
    }

    public Optional<DbGame> decode() {
        if (players.size() < 2) return Optional.empty();
        return players.stream().filter(p -> p.getColor().equals("white")).findFirst().flatMap(RawDbPlayer::decode)
            .flatMap(whitePlayer -> players.stream().filter(p -> p.getColor().equals("black")).findFirst().flatMap(RawDbPlayer::decode)
                .flatMap(blackPlayer -> Status.apply(status)
                    .flatMap(trueStatus -> Color.apply(creatorColor)
                        .flatMap(trueCreatorColor -> Variant.apply(variant)
                            .map(trueVariant -> {
                                Optional<Clock> validClock = Optional.ofNullable(clock).flatMap(RawDbClock::decode);
                                return new DbGame(
                                    id,
                                    whitePlayer,
                                    blackPlayer,
                                    pgn, trueStatus,
                                    turns,
                                    validClock,
                                    Optional.ofNullable(lastMove),
                                    Optional.ofNullable(check).flatMap(Pos::posAt),
                                    trueCreatorColor,
                                    positionHashes,
                                    castles,
                                    isRated,
                                    trueVariant,
                                    Optional.ofNullable(winnerId)
                                );
                })))));
    }

    public static RawDbGame encode(DbGame dbGame) {
        return new RawDbGame(
            dbGame.getId(),
            dbGame.players().stream().map(RawDbPlayer::encode).toList(),
            dbGame.getPgn(),
            dbGame.getStatus().id(),
            dbGame.getTurns(),
            dbGame.getClock().map(RawDbClock::encode).orElse(null),
            dbGame.getLastMove().orElse(null),
            dbGame.getCheck().map(Pos::key).orElse(null),
            dbGame.getCreatorColor().getName(),
            dbGame.getPositionHashes(),
            dbGame.getCastles(),
            dbGame.isRated(),
            dbGame.getVariant().id(),
            dbGame.getWinnerId().orElse(null)
        );
    }
}

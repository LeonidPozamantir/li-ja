package leo.lija.system.entities;


import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import leo.lija.chess.Clock;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Entity
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
    @Setter
    private int turns;
    @Embedded
    @Setter
    private RawDbClock clock;
    private String lastMove;
    private String positionHashes;
    private String castles;
    private boolean isRated;
    private int variant;

    public RawDbGame(String id, List<RawDbPlayer> players, String pgn, int status, int turns, RawDbClock clock, String lastMove, String positionHashes, String castles, boolean isRated, int variant) {
        this.id = id;
        this.players = players;
        this.pgn = pgn;
        this.status = status;
        this.turns = turns;
        this.clock = clock;
        this.lastMove = lastMove;
        this.positionHashes = positionHashes;
        this.castles = castles;
        this.isRated = isRated;
        this.variant = variant;
    }

    public Optional<DbGame> decode() {
        if (players.size() < 2) return Optional.empty();
        return players.stream().filter(p -> p.getColor().equals("white")).findFirst().flatMap(RawDbPlayer::decode)
            .flatMap(whitePlayer -> players.stream().filter(p -> p.getColor().equals("black")).findFirst().flatMap(RawDbPlayer::decode)
                .flatMap(blackPlayer -> Status.fromInt(status)
                    .flatMap(trueStatus -> Variant.apply(variant)
                        .map(trueVariant -> {
                            Optional<Clock> validClock = Optional.ofNullable(clock).flatMap(RawDbClock::decode);
                            return new DbGame(id, whitePlayer, blackPlayer, pgn, trueStatus, turns, validClock, Optional.ofNullable(lastMove), positionHashes, castles, isRated, trueVariant);
                }))));
    }

    public static RawDbGame encode(DbGame dbGame) {
        return new RawDbGame(
            dbGame.getId(),
            dbGame.players().stream().map(RawDbPlayer::encode).toList(),
            dbGame.getPgn(),
            Status.toInt(dbGame.getStatus()),
            dbGame.getTurns(),
            dbGame.getClock().map(RawDbClock::encode).orElse(null),
            dbGame.getLastMove().orElse(null),
            dbGame.getPositionHashes(),
            dbGame.getCastles(),
            dbGame.isRated(),
            dbGame.getVariant().id()
        );
    }
}

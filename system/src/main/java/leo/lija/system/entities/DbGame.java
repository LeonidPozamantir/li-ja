package leo.lija.system.entities;


import io.vavr.collection.HashMap;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import leo.lija.chess.Board;
import leo.lija.chess.Clock;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.History;
import leo.lija.chess.Piece;
import leo.lija.chess.Pos;
import leo.lija.chess.utils.Pair;
import leo.lija.system.Piotr;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.posAt;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Getter
@EqualsAndHashCode
@ToString
public class DbGame {

    @Id
    private String id;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<DbPlayer> players;

    @NotNull
    @Column(nullable = false)
    private String pgn;
    private int status;
    @Setter
    private int turns;
    @Embedded
    private DbClock clock;
    private String lastMove;

    public DbGame(String id, List<DbPlayer> players, String pgn, int status, int turns, DbClock clock, String lastMove) {
        this.id = id;
        this.players = players;
        this.pgn = pgn;
        this.status = status;
        this.turns = turns;
        this.clock = clock;
        this.lastMove = lastMove;
    }

    public DbGame copy() {
        return new DbGame(id, players, pgn, status, turns, clock, lastMove);
    }

    public Optional<DbPlayer> playerById(String id) {
        return Optional.ofNullable(playersById().get(id));
    }

    public Optional<DbPlayer> playerByColor(String color) {
        return Optional.ofNullable(playersByColor().get(color));
    }

    public Optional<String> fullIdOf(DbPlayer player) {
        if (players.contains(player)) return Optional.of(id + player.getId());
        return Optional.empty();
    }

    private Map<String, DbPlayer> playersByColor() {
        if (cachedPlayersByColor.isEmpty()) cachedPlayersByColor = Optional.of(players.stream().collect(Collectors.toMap(DbPlayer::getColor, Function.identity())));
        return cachedPlayersByColor.get();
    }
    private Map<String, DbPlayer> playersById() {
        if (cachedPlayersById.isEmpty()) cachedPlayersById = Optional.of(players.stream().collect(Collectors.toMap(DbPlayer::getId, Function.identity())));
        return cachedPlayersById.get();
    }
    @Transient
    private Optional<Map<String, DbPlayer>> cachedPlayersByColor = Optional.empty();
    @Transient
    private Optional<Map<String, DbPlayer>> cachedPlayersById = Optional.empty();

    public Game toChess() {
        Map<Pos, Piece> pieces = new java.util.HashMap<>();
        Map<Pos, Piece> deads = new java.util.HashMap<>();
        players.forEach(player -> {
            Color color = Color.allByName.get(player.getColor());
            Arrays.stream(player.getPs().split(" ")).toList().forEach(pieceCode -> {
                char[] codes = pieceCode.toCharArray();
                if (codes.length < 2) return;

                char pos = codes[0];
                char role = codes[1];
                if (Character.isUpperCase(role)) {
                    Optional<Pair<Pos, Piece>> optPosPiece = posPiece(pos, Character.toLowerCase(role), color);
                    if (optPosPiece.isEmpty()) return;
                    deads.put(optPosPiece.get().getFirst(), optPosPiece.get().getSecond());
                } else {
                    Optional<Pair<Pos, Piece>> optPosPiece = posPiece(pos, role, color);
                    if (optPosPiece.isEmpty()) return;
                    pieces.put(optPosPiece.get().getFirst(), optPosPiece.get().getSecond());
                }
            });
        });

        Optional<Clock> oc = Optional.ofNullable(clock).map(c -> {
            Color color = Color.of(c.getColor()).get();
            Float whiteTime = c.getTimes().get("white");
            Float blackTime = c.getTimes().get("black");
            return new Clock(color, c.getIncrement(), c.getLimit(), Map.of(WHITE, whiteTime, BLACK, blackTime));
        });
        return new Game(
            new Board(pieces, new History(getLastMoveChess())),
            0 == turns % 2 ? WHITE : BLACK,
            pgn,
            oc,
            HashMap.ofAll(deads)
        );
    }

    private Optional<Pair<Pos, Piece>> posPiece(char posCode, char roleCode, Color color) {
        return Optional.ofNullable(Piotr.decodePos.get(posCode))
            .flatMap(pos -> Optional.ofNullable(Piotr.decodeRole.get(roleCode))
                .map(role -> Pair.of(pos, new Piece(color, role))));
    }

    private Optional<Pair<Pos, Pos>> getLastMoveChess() {
        return Optional.ofNullable(lastMove).flatMap(lm -> {
            Pattern lastMovePattern = Pattern.compile("^([a-h][1-8]) ([a-h][1-8])$");
            Matcher matcher = lastMovePattern.matcher(lm);
            if (matcher.find()) {
                Optional<Pos> from = posAt(matcher.group(1));
                Optional<Pos> to = posAt(matcher.group(2));
                if (from.isEmpty() || to.isEmpty()) return Optional.empty();
                return Optional.of(Pair.of(from.get(), to.get()));
            }
            return Optional.empty();
        });
    }

    public void update(Game game) {
        players = players.stream()
            .map(p -> {
                Color color = Color.allByName.get(p.getColor());
                String newPs = game.getBoard().actorsOf(color).stream()
                    .map(actor -> Piotr.encodePos.get(actor.getPos()).toString() + actor.getPiece().role().fen)
                    .collect(Collectors.joining(" "));
                return new DbPlayer(p.getId(), p.getColor(), newPs, p.getAiLevel(), p.getIsWinner(), p.getEvts(), p.getElo());
            }).toList();
        pgn = game.getPgnMoves();
    }

    public static final int GAME_ID_SIZE = 8;
    public static final int PLAYER_ID_SIZE = 4;
    public static final int FULL_ID_SIZE = 12;
}

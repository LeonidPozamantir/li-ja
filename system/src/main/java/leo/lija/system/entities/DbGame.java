package leo.lija.system.entities;


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
import leo.lija.chess.Move;
import leo.lija.chess.Piece;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.event.Event;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.posAt;
import static leo.lija.system.Utils.MOVE_STRING;

@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Data
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
    @Setter
    private DbClock clock;
    private String lastMove;
    private String positionHashes;

    public DbGame(String id, List<DbPlayer> players, String pgn, int status, int turns, DbClock clock, String lastMove, String positionHashes) {
        this.id = id;
        this.players = players;
        this.pgn = pgn;
        this.status = status;
        this.turns = turns;
        this.clock = clock;
        this.lastMove = lastMove;
        this.positionHashes = positionHashes;
    }

    public DbGame copy() {
        return new DbGame(id, players.stream().map(DbPlayer::copy).toList(), pgn, status, turns, clock, lastMove, positionHashes);
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
        List<Pair<Pos, Piece>> deads = new ArrayList<>();
        players.forEach(player -> {
            Color color = Color.apply(player.getColor()).get();
            Arrays.stream(player.getPs().split(" ")).toList().forEach(pieceCode -> addToPiecesAndDeads(pieceCode, color, pieces, deads));
        });

        Optional<Clock> oc = Optional.ofNullable(clock).map(c -> {
            Color color = Color.apply(c.getColor()).get();
            Float whiteTime = c.getTimes().get("white");
            Float blackTime = c.getTimes().get("black");
            return new Clock(color, c.getIncrement(), c.getLimit(), Map.of(WHITE, whiteTime, BLACK, blackTime));
        });
        return new Game(
            new Board(pieces, new History(getLastMoveChess(), splitPositionHashes())),
            0 == turns % 2 ? WHITE : BLACK,
            pgn,
            oc,
            io.vavr.collection.List.ofAll(deads),
            turns
        );
    }

    private io.vavr.collection.List<String> splitPositionHashes() {
        return io.vavr.collection.List.ofAll(IntStream.range(0, (positionHashes.length() / History.HASH_SIZE))
            .mapToObj(i -> positionHashes.substring(i * History.HASH_SIZE, (i + 1) * History.HASH_SIZE))
            .collect(Collectors.toList()));
    }

    private void addToPiecesAndDeads(String pieceCode, Color color, Map<Pos, Piece> pieces, List<Pair<Pos, Piece>> deads) {
        char[] codes = pieceCode.toCharArray();
        if (codes.length < 2) return;

        char pos = codes[0];
        char role = codes[1];
        if (Character.isUpperCase(role)) {
            Optional<Pair<Pos, Piece>> optPosPiece = posPiece(pos, Character.toLowerCase(role), color);
            if (optPosPiece.isEmpty()) return;
            deads.add(Pair.of(optPosPiece.get().getFirst(), optPosPiece.get().getSecond()));
        } else {
            Optional<Pair<Pos, Piece>> optPosPiece = posPiece(pos, role, color);
            if (optPosPiece.isEmpty()) return;
            pieces.put(optPosPiece.get().getFirst(), optPosPiece.get().getSecond());
        }
    }

    private Optional<Pair<Pos, Piece>> posPiece(char posCode, char roleCode, Color color) {
        return Pos.piotr(posCode)
            .flatMap(pos -> Role.byFen(roleCode)
                .map(role -> Pair.of(pos, new Piece(color, role))));
    }

    private Optional<Pair<Pos, Pos>> getLastMoveChess() {
        return Optional.ofNullable(lastMove).flatMap(lm -> {
            Matcher matcher = MOVE_STRING.matcher(lm);
            if (matcher.find()) {
                Optional<Pos> o = posAt(matcher.group(1));
                Optional<Pos> d = posAt(matcher.group(2));
                if (o.isEmpty() || d.isEmpty()) return Optional.empty();
                return Optional.of(Pair.of(o.get(), d.get()));
            }
            return Optional.empty();
        });
    }

    public void update(Game game, Move move) {
        List<Event> events = new ArrayList<>(Event.fromMove(move));
        events.addAll(Event.fromSituation(game.situation()));
        players = players.stream()
            .map(player -> {
                Color color = Color.apply(player.getColor()).get();
                String newPs = DbPlayer.encodePieces(game.getBoard().getPieces(), game.getDeads(), color);

                List<Event> newEvents = new ArrayList<>(events);
                newEvents.add(Event.possibleMoves(game.situation(), color));
                String newEvts = player.newEvts(newEvents);

                return new DbPlayer(player.getId(), player.getColor(), newPs, player.getAiLevel(), player.getIsWinner(), newEvts, player.getElo());
            }).toList();
        pgn = game.getPgnMoves();
        turns = game.getTurns();
        positionHashes = game.getBoard().getHistory().positionHashes().mkString();
    }

    public static final int GAME_ID_SIZE = 8;
    public static final int PLAYER_ID_SIZE = 4;
    public static final int FULL_ID_SIZE = 12;
}

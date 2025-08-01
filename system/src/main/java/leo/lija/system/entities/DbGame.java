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
import leo.lija.chess.Side;
import leo.lija.chess.Situation;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.event.Event;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
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
    private String castles;
    private boolean isRated;

    public DbGame(String id, List<DbPlayer> players, String pgn, int status, int turns, DbClock clock, String lastMove) {
        this(id, players, pgn, status, turns, clock, lastMove, "", "KQkq", false);
    }

    public DbGame(String id, List<DbPlayer> players, String pgn, int status, int turns, DbClock clock, String lastMove, String positionHashes, String castles, boolean isRated) {
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
    }

    public DbGame copy() {
        return new DbGame(id, players.stream().map(DbPlayer::copy).toList(), pgn, status, turns, clock, lastMove, positionHashes, castles, isRated);
    }

    public Optional<DbPlayer> playerById(String id) {
        return Optional.ofNullable(playersById().get(id));
    }

    public Optional<DbPlayer> playerByColor(String color) {
        return Optional.ofNullable(playersByColor().get(color));
    }

    public DbPlayer player() {
        return playerByColor(0 == turns % 2 ? "white" : "black").get();
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
            new Board(pieces, toChessHistory()),
            0 == turns % 2 ? WHITE : BLACK,
            pgn,
            oc,
            io.vavr.collection.List.ofAll(deads),
            turns
        );
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

    private History toChessHistory() {
        Optional<Pair<Pos, Pos>> historyLastMove = Optional.ofNullable(lastMove).flatMap(lm -> {
            Matcher matcher = MOVE_STRING.matcher(lm);
            if (matcher.find()) {
                Optional<Pos> o = posAt(matcher.group(1));
                Optional<Pos> d = posAt(matcher.group(2));
                if (o.isEmpty() || d.isEmpty()) return Optional.empty();
                return Optional.of(Pair.of(o.get(), d.get()));
            }
            return Optional.empty();
        });

        EnumMap<Color, Pair<Boolean, Boolean>> historyCastles = new EnumMap<>(Map.of(
            WHITE, Pair.of(castles.contains("K"), castles.contains("Q")),
            BLACK, Pair.of(castles.contains("k"), castles.contains("q"))
        ));

        io.vavr.collection.List<String> historyPositionHashes = io.vavr.collection.List.ofAll(IntStream.range(0, (positionHashes.length() / History.HASH_SIZE))
            .mapToObj(i -> positionHashes.substring(i * History.HASH_SIZE, (i + 1) * History.HASH_SIZE))
            .toList());

        return new History(historyLastMove, historyCastles, historyPositionHashes);
    }

    public void update(Game game, Move move) {
        History history = game.getBoard().getHistory();
        Situation situation = game.situation();
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
        positionHashes = history.positionHashes().mkString();
        castles = List.of(
            history.canCastle(WHITE, Side.KING_SIDE) ? "K" : "",
            history.canCastle(WHITE, Side.QUEEN_SIDE) ? "Q" : "",
            history.canCastle(BLACK, Side.KING_SIDE) ? "k" : "",
            history.canCastle(BLACK, Side.QUEEN_SIDE) ? "q" : ""
        ).stream().collect(Collectors.joining());

        if (situation.checkmate()) status = MATE;
        else if (situation.stalemate()) status = STALEMATE;
        else if (situation.autoDraw()) status = DRAW;
    }

    public boolean playable() {
        return status < ABORTED;
    }

    public static final int GAME_ID_SIZE = 8;
    public static final int PLAYER_ID_SIZE = 4;
    public static final int FULL_ID_SIZE = 12;

    private static final int CREATED = 10;
    private static final int STARTED = 20;
    private static final int ABORTED = 25;
    private static final int MATE = 30;
    private static final int RESIGN = 31;
    private static final int STALEMATE = 32;
    private static final int TIMEOUT = 33;
    private static final int DRAW = 34;
    private static final int OUTOFTIME = 35;
    private static final int CHEAT = 36;
}

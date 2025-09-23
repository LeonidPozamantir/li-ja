package leo.lija.system.entities;


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
import leo.lija.system.entities.event.ReloadTableEvent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.posAt;
import static leo.lija.system.Utils.MOVE_STRING;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Data
public class DbGame {

    private String id;
    private DbPlayer whitePlayer;
    private DbPlayer blackPlayer;
    private String pgn;
    private Status status;
    private int turns;
    private Optional<Clock> clock;
    private Optional<String> lastMove;
    private Color creatorColor;
    private String positionHashes;
    private String castles;
    private boolean isRated;
    private Variant variant;

    public DbGame(String id, DbPlayer whitePlayer, DbPlayer blackPlayer, String pgn, Status status, int turns, Optional<Clock> clock, Optional<String> lastMove, Color creatorColor) {
        this(id, whitePlayer, blackPlayer, pgn, status, turns, clock, lastMove, creatorColor, "", "KQkq", false, Variant.STANDARD);
    }

    public DbGame(String id, DbPlayer whitePlayer, DbPlayer blackPlayer, String pgn, Status status, int turns, Optional<Clock> clock, Optional<String> lastMove, Color creatorColor, String positionHashes, String castles, boolean isRated, Variant variant) {
        this.id = id;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.pgn = pgn;
        this.status = status;
        this.turns = turns;
        this.clock = clock;
        this.lastMove = lastMove;
        this.creatorColor = creatorColor;
        this.positionHashes = positionHashes;
        this.castles = castles;
        this.isRated = isRated;
        this.variant = variant;
    }

    public DbGame copy() {
        return new DbGame(id, whitePlayer.copy(), blackPlayer.copy(), pgn, status, turns, clock, lastMove, creatorColor, positionHashes, castles, isRated, variant);
    }

    public List<DbPlayer> players() {
        return List.of(whitePlayer, blackPlayer);
    }

    public Map<Color, DbPlayer> playersByColor() {
        return Map.of(WHITE, whitePlayer, BLACK, blackPlayer);
    }

    public DbPlayer player(Color color) {
        return switch (color) {
            case WHITE -> whitePlayer;
            case BLACK -> blackPlayer;
        };
    }

    public Optional<DbPlayer> player(String playerId) {
        return players().stream().filter(p -> p.getId().equals(playerId)).findFirst();
    }

    public boolean isPlayerFullId(DbPlayer player, String fullId) {
        return fullId.length() == DbGame.FULL_ID_SIZE && player.getId().equals(fullId.substring(8));
    }

    public DbPlayer opponent(DbPlayer p) {
        return player(p.getColor().getOpposite());
    }

    public DbPlayer player() {
        return player(0 == turns % 2 ? WHITE : BLACK);
    }

    public Optional<String> fullIdOf(DbPlayer player) {
        if (players().contains(player)) return Optional.of(id + player.getId());
        return Optional.empty();
    }

    public String fullIdOf(Color color) {
        return id + player(color).getId();
    }

    public Game toChess() {
        Map<Pos, Piece> pieces = new java.util.HashMap<>();
        List<Pair<Pos, Piece>> deads = new ArrayList<>();
        players().forEach(player -> {
            Color color = player.getColor();
            Arrays.stream(player.getPs().split(" ")).toList().forEach(pieceCode -> addToPiecesAndDeads(pieceCode, color, pieces, deads));
        });

        return new Game(
            new Board(pieces, toChessHistory()),
            0 == turns % 2 ? WHITE : BLACK,
            pgn,
            clock,
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
        Optional<Pair<Pos, Pos>> historyLastMove = lastMove.flatMap(lm -> {
            Matcher matcher = MOVE_STRING.matcher(lm);
            if (matcher.find()) {
                Optional<Pos> o = posAt(matcher.group(1));
                Optional<Pos> d = posAt(matcher.group(2));
                if (o.isEmpty() || d.isEmpty()) return Optional.empty();
                return Optional.of(Pair.of(o.get(), d.get()));
            }
            return Optional.empty();
        });

        boolean whiteCastleKingSide = castles.contains("K");
        boolean whiteCastleQueenSide = castles.contains("Q");
        boolean blackCastleKingSide = castles.contains("k");
        boolean blackCastleQueenSide = castles.contains("q");

        io.vavr.collection.List<String> historyPositionHashes = io.vavr.collection.List.ofAll(IntStream.range(0, (positionHashes.length() / History.HASH_SIZE))
            .mapToObj(i -> positionHashes.substring(i * History.HASH_SIZE, (i + 1) * History.HASH_SIZE))
            .toList());

        return new History(historyLastMove, historyPositionHashes, whiteCastleKingSide, whiteCastleQueenSide, blackCastleKingSide, blackCastleQueenSide);
    }

    public void update(Game game, Move move) {
        boolean abortableBefore = abortable();
        boolean whiteCanOfferDrawBefore = playerCanOfferDraw(WHITE);
        boolean blackCanOfferDrawBefore = playerCanOfferDraw(BLACK);

        History history = game.getBoard().getHistory();
        Situation situation = game.situation();
        List<Event> events = new ArrayList<>(Event.fromMove(move));
        events.addAll(Event.fromSituation(game.situation()));
        whitePlayer = updatePlayer(game, whitePlayer, events);
        blackPlayer = updatePlayer(game, blackPlayer, events);
        pgn = game.getPgnMoves();
        turns = game.getTurns();
        positionHashes = history.positionHashes().mkString();
        castles = List.of(
            history.canCastle(WHITE, Side.KING_SIDE) ? "K" : "",
            history.canCastle(WHITE, Side.QUEEN_SIDE) ? "Q" : "",
            history.canCastle(BLACK, Side.KING_SIDE) ? "k" : "",
            history.canCastle(BLACK, Side.QUEEN_SIDE) ? "q" : ""
        ).stream().collect(Collectors.joining());

        if (situation.checkmate()) status = Status.MATE;
        else if (situation.stalemate()) status = Status.STALEMATE;
        else if (situation.autoDraw()) status = Status.DRAW;
        clock = game.getClock();

        if (abortableBefore != abortable() || whiteCanOfferDrawBefore != playerCanOfferDraw(WHITE) || blackCanOfferDrawBefore != playerCanOfferDraw(BLACK)) {
            withEvents(List.of(new ReloadTableEvent()));
        }
    }

    private DbPlayer updatePlayer(Game game, DbPlayer player, List<Event> events) {
        String newPs = player.encodePieces(game.getBoard().getPieces(), game.getDeads());

        List<Event> newEvents = new ArrayList<>(events);
        newEvents.add(Event.possibleMoves(game.situation(), player.getColor()));
        String newEvts = player.newEvts(newEvents);

        return new DbPlayer(player.getId(), player.getColor(), newPs, player.getAiLevel(), player.getIsWinner(), newEvts, player.getElo(), player.getIsOfferingDraw(), player.getLastDrawOffer());
    }

    public void withEvents(List<Event> events) {
        whitePlayer.withEvents(events);
        blackPlayer.withEvents(events);
    }

    public void withEvents(Color color, List<Event> events) {
        if (color == WHITE) withEvents(events, List.of());
        else if (color == BLACK) withEvents(List.of(), events);
    }

    public void withEvents(List<Event> whiteEvents, List<Event> blackEvents) {
        whitePlayer.withEvents(whiteEvents);
        blackPlayer.withEvents(blackEvents);
    }

    public boolean playable() {
        return status.id() < Status.ABORTED.id();
    }

    public Optional<Integer> aiLevel() {
        return players().stream().filter(DbPlayer::isAi).findAny()
            .flatMap(p -> Optional.ofNullable(p.getAiLevel()));
    }

    public DbGame mapPlayers(UnaryOperator<DbPlayer> f) {
        whitePlayer = f.apply(whitePlayer);
        blackPlayer = f.apply(blackPlayer);
        return this;
    }

    public boolean playerCanOfferDraw(Color color) {
        return status.id() >= Status.STARTED.id()
            && status.id() < Status.ABORTED.id()
            && turns >= 2
            && !player(color).getIsOfferingDraw()
            && !playerHasOfferedDraw(color);
    }

    public boolean playerHasOfferedDraw(Color color) {
        return player(color).getLastDrawOffer() != null && player(color).getLastDrawOffer() >= turns - 1;
    }

    public boolean abortable() {
        return status == Status.STARTED && turns < 2;
    }

    public static final int GAME_ID_SIZE = 8;
    public static final int PLAYER_ID_SIZE = 4;
    public static final int FULL_ID_SIZE = 12;

}

package leo.lija.app.entities;


import leo.lija.app.entities.event.ClockEvent;
import leo.lija.app.entities.event.StateEvent;
import leo.lija.chess.Board;
import leo.lija.chess.Clock;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.History;
import leo.lija.chess.Move;
import leo.lija.chess.Piece;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.Situation;
import leo.lija.chess.utils.Pair;
import leo.lija.app.entities.event.EndEvent;
import leo.lija.app.entities.event.Event;
import leo.lija.app.entities.event.ReloadTableEvent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.stream.IntStream;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.posAt;
import static leo.lija.app.Utils.MOVE_STRING;

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
    private Optional<Pos> check;
    private Color creatorColor;
    private String positionHashes;
    private String castles;
    private boolean isRated;
    private Variant variant;
    private Optional<Long> lastMoveTime;

    public DbGame(String id, DbPlayer whitePlayer, DbPlayer blackPlayer, String pgn, Status status, int turns, Optional<Clock> clock, Optional<String> lastMove, Optional<Pos> check, Color creatorColor) {
        this(id, whitePlayer, blackPlayer, pgn, status, turns, clock, lastMove, check, creatorColor, "", "KQkq", false, Variant.STANDARD, Optional.empty());
    }

    public DbGame(String id, DbPlayer whitePlayer, DbPlayer blackPlayer, String pgn, Status status, int turns, Optional<Clock> clock, Optional<String> lastMove, Optional<Pos> check, Color creatorColor, String positionHashes, String castles, boolean isRated, Variant variant, Optional<Long> lastMoveTime) {
        this.id = id;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
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
        this.lastMoveTime = lastMoveTime;
    }

    public DbGame copy() {
        return new DbGame(id, whitePlayer.copy(), blackPlayer.copy(), pgn, status, turns, clock, lastMove, check, creatorColor, positionHashes, castles, isRated, variant, lastMoveTime);
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

    public History toChessHistory() {
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

    public Progress update(Game game, Move move) {
        return update(game, move, false);
    }

    public Progress update(Game game, Move move, boolean blur) {
        boolean abortableBefore = abortable();
        boolean whiteCanOfferDrawBefore = playerCanOfferDraw(WHITE);
        boolean blackCanOfferDrawBefore = playerCanOfferDraw(BLACK);

        History history = game.getBoard().getHistory();
        Situation situation = game.situation();
        List<Event> events = new ArrayList<>(List.of(
            Event.possibleMoves(situation, WHITE),
            Event.possibleMoves(situation, BLACK),
            new StateEvent(game.situation().getColor(), game.getTurns())
        ));
        events.addAll(Event.fromMove(move));
        events.addAll(Event.fromSituation(game.situation()));

        blackPlayer = copyPlayer(game, blackPlayer, move, blur);
        whitePlayer = copyPlayer(game, whitePlayer, move, blur);

        pgn = game.getPgnMoves();
        turns = game.getTurns();
        positionHashes = history.positionHashes().mkString();
        castles = history.castleNotation();
        lastMove = history.lastMove().map(p -> p.getFirst() + " " + p.getSecond());

        if (situation.checkmate()) status = Status.MATE;
        else if (situation.stalemate()) status = Status.STALEMATE;
        else if (situation.autoDraw()) status = Status.DRAW;
        clock = game.getClock();
        check = game.situation().check() ? game.situation().kingPos() : Optional.empty();
        lastMoveTime = recordMoveTimes() ? Optional.of(nowSeconds()) : Optional.empty();

        clock.ifPresent(c -> events.addLast(ClockEvent.apply(c)));
        if (playable() && (abortableBefore != abortable()
                || whiteCanOfferDrawBefore != playerCanOfferDraw(WHITE)
                || blackCanOfferDrawBefore != playerCanOfferDraw(BLACK))) {
            events.addAll(Color.all.stream().map(ReloadTableEvent::new).toList());
        }
        return new Progress(this, events);
    }

    private DbPlayer copyPlayer(Game game, DbPlayer player, Move move, boolean blur) {
        String newPs = player.encodePieces(game.getBoard().getPieces(), game.getDeads());
        Integer newBlurs = player.getBlurs() + (blur && move.color() == player.getColor() ? 1 : 0);
        String newMoveTimes = recordMoveTimes() && move.color() == player.getColor()
            ? lastMoveTime.map(
                    lmt -> {
                        long mt = nowSeconds() - lmt;
                        return player.getMoveTimes().isEmpty()
                            ? String.valueOf(mt)
                            : player.getMoveTimes() + " " + mt;
                    }
                ).orElse("")
            : player.getMoveTimes();
        return new DbPlayer(player.getId(), player.getColor(), newPs, player.getAiLevel(), player.getIsWinner(), player.getElo(), player.getIsOfferingDraw(), player.getLastDrawOffer(), player.getUserId(), newMoveTimes, newBlurs);
    }

    private long nowSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public void updatePlayer(Color color, UnaryOperator<DbPlayer> f) {
        if (color == WHITE) whitePlayer = f.apply(whitePlayer);
        if (color == BLACK) blackPlayer = f.apply(blackPlayer);
    }

    private boolean recordMoveTimes() {
        return !hasAi();
    }

    public boolean playable() {
        return status.id() < Status.ABORTED.id();
    }

    public boolean playableBy(DbPlayer p) {
        return playable() && player().getUserId().equals(p.getUserId());
    }

    public Optional<Integer> aiLevel() {
        return players().stream().filter(DbPlayer::isAi).findAny()
            .flatMap(DbPlayer::getAiLevel);
    }

    // Leo: make lazy val
    private boolean hasAi() {
        return players().stream().anyMatch(DbPlayer::isAi);
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
        return player(color).getLastDrawOffer().isPresent() && player(color).getLastDrawOffer().get() >= turns - 1;
    }

    public boolean abortable() {
        return status == Status.STARTED && turns < 2;
    }

    public boolean resignable() {
        return playable() && abortable();
    }

    public Progress finish(Status status, Optional<Color> winner) {
        this.status = status;
        whitePlayer = whitePlayer.finish(winner.isPresent() && winner.get() == WHITE);
        blackPlayer = blackPlayer.finish(winner.isPresent() && winner.get() == BLACK);
        clock = clock.map(Clock::stop);
        return new Progress(this, List.of(new EndEvent()));
    }

    public boolean rated() {
        return isRated;
    }

    public boolean finished() {
        return status.id() >= Status.MATE.id();
    }

    public Optional<Color> winnerColor() {
        return players().stream().filter(DbPlayer::wins).findFirst().map(DbPlayer::getColor);
    }

    public Optional<DbPlayer> outoftimePlayer() {
        return clock
            .filter(c -> playable())
            .filter(c -> !c.isRunning() || c.outoftime(player().getColor()))
            .map(c -> player());
    }

    public Progress withClock(Clock c) {
        clock = Optional.of(c);
        return new Progress(this);
    }

    public DbPlayer creator() {
        return player(creatorColor);
    }

    public DbPlayer invited() {
        return player(creatorColor.getOpposite());
    }

    public static final int GAME_ID_SIZE = 8;
    public static final int PLAYER_ID_SIZE = 4;
    public static final int FULL_ID_SIZE = 12;

    public static String takeGameId(String fullId) {
        return fullId.substring(0, Math.min(GAME_ID_SIZE, fullId.length()));
    }

}

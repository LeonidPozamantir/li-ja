package leo.lija.app;

import leo.lija.app.db.GameRepo;
import leo.lija.app.db.HistoryRepo;
import leo.lija.app.db.UserRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Pov;
import leo.lija.app.entities.Progress;
import leo.lija.app.entities.Status;
import leo.lija.app.entities.User;
import leo.lija.app.entities.event.Event;
import leo.lija.app.exceptions.AppException;
import leo.lija.app.memo.FinisherLock;
import leo.lija.chess.Color;
import leo.lija.chess.EloCalculator;
import leo.lija.chess.utils.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;

@Service
public class Finisher {

    private final HistoryRepo historyRepo;
    private final UserRepo userRepo;
    private final GameRepo gameRepo;
    private final Messenger messenger;
    private final EloCalculator eloCalculator = new EloCalculator();
    private final FinisherLock finisherLock;

    public Finisher(HistoryRepo historyRepo, UserRepo userRepo, GameRepo gameRepo, Messenger messenger, FinisherLock finisherLock) {
        this.historyRepo = historyRepo;
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.messenger = messenger;
        this.finisherLock = finisherLock;
    }

    public List<Event> abort(Pov pov) {
        if (pov.game().abortable()) return finish(pov.game(), Status.ABORTED);
        throw new AppException("game is not abortable");
    }

    public List<Event> resign(Pov pov) {
        if (pov.game().resignable()) return finish(pov.game(), Status.RESIGN, Optional.of(pov.color().getOpposite()));
        throw new AppException("game is not resignable");
    }

    public List<Event> drawClaim(Pov pov) {
        DbGame game = pov.game();
        Color color = pov.color();
        if (game.playable() && game.player().getColor() == color && game.toChessHistory().threefoldRepetition())
            finish(game, Status.DRAW);
        throw new AppException("game is not threefold repetition");
    }

    public List<Event> drawAccept(Pov pov) {
        if (pov.opponent().getIsOfferingDraw()) return finish(pov.game(), Status.DRAW, Optional.empty(), Optional.of("Draw offer accepted"));
        throw new AppException("opponent is not proposing a draw");
    }

    public List<Event> outoftime(DbGame game) {
        return game.outoftimePlayer().map(player ->
            finish(game, Status.OUTOFTIME,
                Optional.of(player.getColor().getOpposite()).filter((c) -> game.toChess().getBoard().hasEnoughMaterialToMate(c)))
        ).orElseThrow(() -> new AppException("no outoftime applicable " + game.getClock()));
    }

    public void outoftimes(List<DbGame> games) {
        games.forEach(this::outoftime);
    }

    public List<Event> moveFinish(DbGame game, Color color) {
        if (game.getStatus() == Status.MATE) return finish(game, Status.MATE, Optional.of(color));
        if (game.getStatus() == Status.STALEMATE || game.getStatus() == Status.DRAW) return finish(game, game.getStatus());
        return List.of();
    }

    private List<Event> finish(DbGame game, Status status) {
        return finish(game, status, Optional.empty(), Optional.empty());
    }

    private List<Event> finish(DbGame game, Status status, Optional<Color> winner) {
        return finish(game, status, winner, Optional.empty());
    }

    private List<Event> finish(DbGame game, Status status, Optional<Color> winner, Optional<String> message) {
        if (finisherLock.isLocked(game)) throw new AppException("game finish is locked");
        finisherLock.lock(game);
        Progress p1 = game.finish(status, winner);
        message.ifPresent(m -> p1.addAll(messenger.systemMessage(p1.game(), m)));
        gameRepo.save(p1);
        Optional<String> winnerId = winner.flatMap(c -> p1.game().player(c).getUserId());
        gameRepo.finish(p1.game().getId(), winnerId);
        updateElo(p1.game());
        incNbGames(p1.game(), WHITE);
        incNbGames(p1.game(), BLACK);
        return p1.events();
    }

    private void incNbGames(DbGame game, Color color) {
        game.player(color).getUserId().ifPresent(
            id -> userRepo.incNbGames(id, game.rated())
        );
    }

    private void updateElo(DbGame game) {
        if (!game.finished() || !game.rated() || game.getTurns() < 2) return;
        game.player(WHITE).getUserId()
            .flatMap(whiteUserId -> game.player(BLACK).getUserId()
                .filter(blackUserId -> !whiteUserId.equals(blackUserId))
                .map(blackUserId -> {
                    Optional<User> whiteUserOption = userRepo.user(whiteUserId);
                    Optional<User> blackUserOption = userRepo.user(blackUserId);
                    if (whiteUserOption.isEmpty() || blackUserOption.isEmpty()) return null;
                    User whiteUser = whiteUserOption.get();
                    User blackUser = blackUserOption.get();
                    Pair<Integer, Integer> elos = eloCalculator.calculate(
                        new EloCalculator.User(whiteUser.getElo(), whiteUser.getNbRatedGames()),
                        new EloCalculator.User(blackUser.getElo(), blackUser.getNbRatedGames()),
                        game.winnerColor()
                    );
                    int whiteElo = elos.getFirst();
                    int blackElo = elos.getSecond();
                    gameRepo.setEloDiffs(game.getId(), whiteElo - whiteUser.getElo(), blackElo - blackUser.getElo());
                    userRepo.setElo(whiteUserId, whiteElo);
                    userRepo.setElo(blackUserId, blackElo);
                    historyRepo.addEntry(whiteUser.getUserNameCanonical(), whiteElo, game.getId());
                    historyRepo.addEntry(blackUser.getUserNameCanonical(), blackElo, game.getId());
                    return null;
                })
            );
    }
}

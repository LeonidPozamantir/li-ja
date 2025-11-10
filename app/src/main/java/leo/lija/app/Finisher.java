package leo.lija.app;

import leo.lija.app.db.GameRepo;
import leo.lija.app.db.HistoryRepo;
import leo.lija.app.db.UserRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Evented;
import leo.lija.app.entities.Pov;
import leo.lija.app.entities.Status;
import leo.lija.app.entities.User;
import leo.lija.app.exceptions.AppException;
import leo.lija.app.memo.AliveMemo;
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
public class Finisher extends IOTools {

    private final HistoryRepo historyRepo;
    private final UserRepo userRepo;
    private final Messenger messenger;
    private final AliveMemo aliveMemo;
    private final EloCalculator eloCalculator = new EloCalculator();
    private final FinisherLock finisherLock;

    public Finisher(GameRepo gameRepo, HistoryRepo historyRepo, UserRepo userRepo, Messenger messenger, AliveMemo aliveMemo, FinisherLock finisherLock) {
        super(gameRepo);
        this.historyRepo = historyRepo;
        this.userRepo = userRepo;
        this.messenger = messenger;
        this.aliveMemo = aliveMemo;
        this.finisherLock = finisherLock;
    }

    public void abort(Pov pov) {
        if (pov.game().abortable()) finish(pov.game(), Status.ABORTED);
        else throw new AppException("game is not abortable");
    }

    public void resign(Pov pov) {
        if (pov.game().resignable()) finish(pov.game(), Status.RESIGN, Optional.of(pov.color().getOpposite()));
        else throw new AppException("game is not resignable");
    }

    public void forceResign(Pov pov) {
        if (pov.game().playable() && aliveMemo.inactive(pov.game().getId(), pov.color().getOpposite())) {
            finish(pov.game(), Status.TIMEOUT, Optional.of(pov.color()));
        } else throw new AppException("game is not force-resignable");
    }

    public void drawClaim(Pov pov) {
        DbGame game = pov.game();
        Color color = pov.color();
        if (game.playable() && game.player().getColor() == color && game.toChessHistory().threefoldRepetition()) {
            finish(game, Status.DRAW);
        } else throw new AppException("game is not threefold repetition");
    }

    public void drawAccept(Pov pov) {
        if (pov.opponent().getIsOfferingDraw()) finish(pov.game(), Status.DRAW, Optional.empty(), Optional.of("Draw offer accepted"));
        else throw new AppException("opponent is not proposing a draw");
    }

    public void outoftime(DbGame game) {
        game.outoftimePlayer().ifPresentOrElse(player ->
            finish(game, Status.OUTOFTIME,
                Optional.of(player.getColor().getOpposite()).filter((c) -> game.toChess().getBoard().hasEnoughMaterialToMate(c))),
            () -> {
                throw new AppException("no outoftime applicable " + game.getClock());
            });
    }

    public void outoftimes(List<DbGame> games) {
        games.forEach(this::outoftime);
    }

    public void moveFinish(DbGame game, Color color) {
        if (game.getStatus() == Status.MATE) finish(game, Status.MATE, Optional.of(color));
        else if (game.getStatus() == Status.STALEMATE || game.getStatus() == Status.DRAW) finish(game, game.getStatus());
    }

    private void finish(DbGame game, Status status) {
        finish(game, status, Optional.empty(), Optional.empty());
    }

    private void finish(DbGame game, Status status, Optional<Color> winner) {
        finish(game, status, winner, Optional.empty());
    }

    private void finish(DbGame game, Status status, Optional<Color> winner, Optional<String> message) {
        if (finisherLock.isLocked(game)) throw new AppException("game finish is locked");
        finisherLock.lock(game);
        Evented e1 = game.finish(status, winner);
        message.ifPresent(m -> e1.addAll(messenger.systemMessage(e1.game(), m)));
        save(e1);
        Optional<String> winnerId = winner.flatMap(c -> e1.game().player(c).getUserId());
        gameRepo.finish(e1.game().getId(), winnerId);
        updateElo(e1.game());
        incNbGames(e1.game(), WHITE);
        incNbGames(e1.game(), BLACK);
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
                    User whiteUser = userRepo.user(whiteUserId);
                    User blackUser = userRepo.user(blackUserId);
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

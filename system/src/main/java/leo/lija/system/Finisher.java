package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.chess.EloCalculator;
import leo.lija.chess.utils.Pair;
import leo.lija.system.db.GameRepo;
import leo.lija.system.db.HistoryRepo;
import leo.lija.system.db.UserRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.Status;
import leo.lija.system.entities.User;
import leo.lija.system.exceptions.AppException;
import leo.lija.system.memo.FinisherLock;
import leo.lija.system.memo.VersionMemo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;

@Service
@RequiredArgsConstructor
public class Finisher {

    private final HistoryRepo historyRepo;
    private final UserRepo userRepo;
    private final GameRepo gameRepo;
    private final VersionMemo versionMemo;
    private final EloCalculator eloCalculator = new EloCalculator();
    private final FinisherLock finisherLock;

    public void abort(DbGame game) {
        if (game.abortable()) finish(game, Status.ABORTED);
        else throw new AppException("game is not abortable");
    }

    public void resign(DbGame game, Color color) {
        if (game.resignable()) finish(game, Status.RESIGN, Optional.of(color.getOpposite()));
        else throw new AppException("game is not resignable");
    }

    public void outoftime(DbGame game) {
        game.outoftimePlayer().map(player -> {
            finish(game, Status.OUTOFTIME,
                Optional.of(player.getColor().getOpposite()).filter((c) -> game.toChess().getBoard().hasEnoughMaterialToMate(c)));
            return null;
        }).orElseThrow(() -> new AppException("no outoftime applicable"));
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
        DbGame g2 = game.finish(status, winner, message);
        gameRepo.save(g2);
        versionMemo.put(g2);
        updateElo(g2);
        incNbGames(g2, WHITE);
        incNbGames(g2, BLACK);
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
                    userRepo.setElo(whiteUserId, whiteElo);
                    userRepo.setElo(blackUserId, blackElo);
                    historyRepo.addEntry(whiteUser.getUsername(), whiteElo, game.getId());
                    historyRepo.addEntry(blackUser.getUsername(), blackElo, game.getId());
                    return null;
                })
            );
    }
}

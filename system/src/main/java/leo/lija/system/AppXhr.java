package leo.lija.system;

import leo.lija.chess.Clock;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.utils.Pair;
import leo.lija.system.db.GameRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.Pov;
import leo.lija.system.entities.event.MoretimeEvent;
import leo.lija.system.entities.event.ReloadTableEvent;
import leo.lija.system.exceptions.AppException;
import leo.lija.system.memo.AliveMemo;
import leo.lija.system.memo.VersionMemo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static leo.lija.chess.Pos.posAt;

@Service
public class AppXhr extends IOTools {

    private final Messenger messenger;
    private final Ai ai;
    private final Finisher finisher;
    private final AliveMemo aliveMemo;
    private final int moretimeSeconds;

    public AppXhr(
            GameRepo gameRepo,
            Messenger messenger,
            Ai ai,
            Finisher finisher,
            VersionMemo versionMemo,
            AliveMemo aliveMemo,
            @Value("${moretime.seconds}") int moretimeSeconds) {
        super(gameRepo, versionMemo);
        this.messenger = messenger;
        this.ai = ai;
        this.finisher = finisher;
        this.aliveMemo = aliveMemo;
        this.moretimeSeconds = moretimeSeconds;
    }

    public void play(String fullId, String fromString, String toString) {
        play(fullId, fromString, toString, Optional.empty());
    }

    public void play(String fullId, String origString, String destString, Optional<String> promString) {
        attempt(fullId, pov -> {
            DbGame g1 = pov.game();
            Color color = pov.color();

            if (!g1.playable()) throw new AppException("Game is not playable");
            Pos orig = posAt(origString).orElseThrow(() -> new AppException("Wrong orig " + origString));
            Pos dest = posAt(destString).orElseThrow(() -> new AppException("Wrong dest " + destString));
            Role promotion = promString.map(ps -> Role.promotable(promString).orElseThrow(() -> new AppException("Wrong promotion " + promString))).orElse(null);
            Pair<Game, Move> newChessGameAndMove = g1.toChess().apply(orig, dest, promotion);
            Game newChessGame = newChessGameAndMove.getFirst();
            Move move = newChessGameAndMove.getSecond();
            g1.update(newChessGame, move);
            aliveMemo.put(g1.getId(), color);

            if (g1.finished()) {
                save(g1);
                finisher.moveFinish(g1, color);
            } else if (g1.player().isAi() && g1.playable()) {
                Pair<Game, Move> aiResult;
                try {
                    aiResult = ai.apply(g1);
                } catch (Exception e) {
                    throw new AppException("AI failure");
                }
                newChessGame = aiResult.getFirst();
                move = aiResult.getSecond();
                g1.update(newChessGame, move);
                save(g1);
                finisher.moveFinish(g1, color.getOpposite());
            } else save(g1);
        });
    }

    public void abort(String fullId) {
        attempt(fullId, finisher::abort);
    }

    public void resign(String fullId) {
        attempt(fullId, finisher::resign);
    }

    public void forceResign(String fullId) {
        attempt(fullId, finisher::forceResign);
    }

    public void outoftime(String fullId) {
        attempt(fullId, p -> finisher.outoftime(p.game()));
    }

    public void drawClaim(String fullId) {
        attempt(fullId, finisher::drawClaim);
    }

    public void drawAccept(String fullId) {
        attempt(fullId, finisher::drawAccept);
    }

    public void drawOffer(String fullId) {
        attempt(fullId, pov -> {
            DbGame game = pov.game();
            Color color = pov.color();
            if (game.playerCanOfferDraw(color)) {
                if (game.player(color.getOpposite()).getIsOfferingDraw()) finisher.drawAccept(pov);
                else {
                    messenger.systemMessages(game, "Draw offer sent");
                    game.updatePlayer(color, p -> p.offerDraw(game.getTurns()));
                    game.withEvents(color.getOpposite(), List.of(new ReloadTableEvent()));
                    save(game);
                }
            } else {
                throw new AppException("invalid draw offer " + fullId);
            }
        });
    }

    public void drawCancel(String fullId) {
        attempt(fullId, pov -> {
            DbGame game = pov.game();
            Color color = pov.color();
            if (pov.player().getIsOfferingDraw()) {
                messenger.systemMessages(game, "Draw offer cancelled");
                game.updatePlayer(color, p -> {
                    DbPlayer res = p.copy();
                    res.setIsOfferingDraw(false);
                    return res;
                });
                game.withEvents(color.getOpposite(), List.of(new ReloadTableEvent()));
                save(game);
            } else {
                throw new AppException("no draw offer to cancel " + fullId);
            }
        });
    }

    public void drawDecline(String fullId) {
        attempt(fullId, pov -> {
            DbGame game = pov.game();
            Color color = pov.color();
            if (game.player(color.getOpposite()).getIsOfferingDraw()) {
                messenger.systemMessages(game, "Draw offer declined");
                game.updatePlayer(color.getOpposite(), p -> {
                    DbPlayer res = p.copy();
                    res.setIsOfferingDraw(false);
                    return res;
                });
                game.withEvents(color.getOpposite(), List.of(new ReloadTableEvent()));
                save(game);
            } else {
                throw new AppException("no draw offer to decline " + fullId);
            }
        });
    }

    public void talk(String fullId, String message) {
        attempt(fullId, pov -> {
            messenger.playerMessage(pov.game(), pov.color(), message);
            save(pov.game());
        });
    }

    public float moretime(String fullId) {
        return fromPov(fullId, pov -> {
           return pov.game().getClock().filter(c -> pov.game().playable()).map(clock -> {
               Color color = pov.color().getOpposite();
               Clock newClock = clock.giveTime(color, moretimeSeconds);
               pov.game().withEvents(List.of(new MoretimeEvent(color, moretimeSeconds)));
               pov.game().withClock(newClock);
               messenger.systemMessage(pov.game(), "%s + %d seconds".formatted(color, moretimeSeconds));
               save(pov.game());
               return newClock.remainingTime(color);
           }).orElseThrow(() -> new AppException("cannot add more time"));
        });
    }

    private void attempt(String fullId, Consumer<Pov> action) {
        Pov pov = gameRepo.pov(fullId);
        action.accept(pov);
    }

    private <A> A fromPov(String fullId, Function<Pov, A> op) {
        Pov pov = gameRepo.pov(fullId);
        return op.apply(pov);
    }

}

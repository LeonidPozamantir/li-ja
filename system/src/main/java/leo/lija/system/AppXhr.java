package leo.lija.system;

import leo.lija.chess.Clock;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.utils.Pair;
import leo.lija.system.db.GameRepo;
import leo.lija.system.db.RoomRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.Pov;
import leo.lija.system.entities.event.MessageEvent;
import leo.lija.system.entities.event.MoretimeEvent;
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

            if (g1.player().isAi() && g1.playable()) {
                Pair<Game, Move> aiResult;
                try {
                    aiResult = ai.apply(g1);
                } catch (Exception e) {
                    throw new AppException("AI failure");
                }
                newChessGame = aiResult.getFirst();
                move = aiResult.getSecond();
                g1.update(newChessGame, move);
            }
            save(g1);
            aliveMemo.put(g1.getId(), color);
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

    public void claimDraw(String fullId) {
        attempt(fullId, finisher::claimDraw);
    }

    public void outoftime(String fullId) {
        attempt(fullId, finisher::outoftime);
    }

    public void drawAccept(String fullId) {
        attempt(fullId, finisher::drawAccept);
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

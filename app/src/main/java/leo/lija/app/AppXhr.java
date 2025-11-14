package leo.lija.app;

import leo.lija.app.ai.AiService;
import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.DbPlayer;
import leo.lija.app.entities.Progress;
import leo.lija.app.entities.Pov;
import leo.lija.app.entities.event.Event;
import leo.lija.app.entities.event.MoretimeEvent;
import leo.lija.app.entities.event.ReloadTableEvent;
import leo.lija.app.exceptions.AppException;
import leo.lija.app.game.Socket;
import leo.lija.app.memo.AliveMemo;
import leo.lija.chess.Clock;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.utils.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static leo.lija.chess.Pos.posAt;

@Service
public class AppXhr {

    private final GameRepo gameRepo;
    private final Socket gameSocket;
    private final Messenger messenger;
    private final AiService ai;
    private final Finisher finisher;
    private final AliveMemo aliveMemo;
    private final int moretimeSeconds;

    public AppXhr(
            GameRepo gameRepo,
            @Qualifier("gameSocket") Socket gameSocket,
            Messenger messenger,
            AiService ai,
            Finisher finisher,
            AliveMemo aliveMemo,
            @Value("${moretime.seconds}") int moretimeSeconds) {
        this.gameRepo = gameRepo;
        this.gameSocket = gameSocket;
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
            Progress progress = g1.update(newChessGame, move);
            aliveMemo.put(progress.game().getId(), color);

            if (progress.game().finished()) {
                saveSend(progress);
                finisher.moveFinish(progress.game(), color);
            } else if (progress.game().player().isAi() && progress.game().playable()) {
                Pair<Game, Move> aiResult;
                try {
                    aiResult = ai.apply(progress.game());
                } catch (Exception e) {
                    throw new AppException("AI failure");
                }
                Game newChessGame2 = aiResult.getFirst();
                Move move2 = aiResult.getSecond();
                Progress progress2 = progress.flatMap(g -> g.update(newChessGame2, move2));
                saveSend(progress2);
                finisher.moveFinish(progress2.game(), color.getOpposite());
            } else saveSend(progress);
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
            DbGame g1 = pov.game();
            Color color = pov.color();
            if (g1.playerCanOfferDraw(color)) {
                if (g1.player(color.getOpposite()).getIsOfferingDraw()) finisher.drawAccept(pov);
                else {
                    List<Event> events = new ArrayList<>(List.of(new ReloadTableEvent(color.getOpposite())));
                    events.addAll(messenger.systemMessages(g1, "Draw offer sent"));
                    Progress p1 = new Progress(g1, events);
                    g1.updatePlayer(color, p -> p.offerDraw(g1.getTurns()));
                    saveSend(p1);
                }
            } else {
                throw new AppException("invalid draw offer " + fullId);
            }
        });
    }

    public void drawCancel(String fullId) {
        attempt(fullId, pov -> {
            DbGame g1 = pov.game();
            Color color = pov.color();
            if (pov.player().getIsOfferingDraw()) {
                List<Event> events = new ArrayList<>(List.of(new ReloadTableEvent(color.getOpposite())));
                events.addAll(messenger.systemMessages(g1, "Draw offer cancelled"));
                Progress p1 = new Progress(g1, events);
                g1.updatePlayer(color, DbPlayer::removeDrawOffer);
                saveSend(p1);
            } else {
                throw new AppException("no draw offer to cancel " + fullId);
            }
        });
    }

    public void drawDecline(String fullId) {
        attempt(fullId, pov -> {
            DbGame g1 = pov.game();
            Color color = pov.color();
            if (g1.player(color.getOpposite()).getIsOfferingDraw()) {
                List<Event> events = new ArrayList<>(List.of(new ReloadTableEvent(color.getOpposite())));
                events.addAll(messenger.systemMessages(g1, "Draw offer declined"));
                Progress p1 = new Progress(g1, events);
                g1.updatePlayer(color, DbPlayer::removeDrawOffer);
                saveSend(p1);
            } else {
                throw new AppException("no draw offer to decline " + fullId);
            }
        });
    }

    public float moretime(String fullId) {
        return fromPov(fullId, pov ->
            pov.game().getClock().filter(c -> pov.game().playable()).map(clock -> {
                Color color = pov.color().getOpposite();
                Clock newClock = clock.giveTime(color, moretimeSeconds);
                pov.game().withClock(newClock);
                List<Event> events = new ArrayList<>(List.of(new MoretimeEvent(color, moretimeSeconds)));
                events.addAll(messenger.systemMessage(pov.game(), "%s + %d seconds".formatted(color, moretimeSeconds)));
                Progress p1 = new Progress(pov.game(), events);
                saveSend(p1);
                return newClock.remainingTime(color);
        }).orElseThrow(() -> new AppException("cannot add more time")));
    }

    private void saveSend(Progress progress) {
        gameRepo.save(progress);
        gameSocket.send(progress);
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

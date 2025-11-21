package leo.lija.app;

import leo.lija.app.ai.AiService;
import leo.lija.app.db.GameRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.DbPlayer;
import leo.lija.app.entities.Pov;
import leo.lija.app.entities.PovRef;
import leo.lija.app.entities.Progress;
import leo.lija.app.entities.event.ClockEvent;
import leo.lija.app.entities.event.Event;
import leo.lija.app.entities.event.ReloadTableEvent;
import leo.lija.app.exceptions.AppException;
import leo.lija.chess.Clock;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.Move;
import leo.lija.chess.Pos;
import leo.lija.chess.Role;
import leo.lija.chess.utils.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static leo.lija.chess.Pos.posAt;

@Service
public class Hand {

    private final GameRepo gameRepo;
    private final Messenger messenger;
    private final AiService ai;
    private final Finisher finisher;
    private final int moretimeSeconds;

    public Hand(
            GameRepo gameRepo,
            Messenger messenger,
            AiService ai,
            Finisher finisher,
            @Value("${moretime.seconds}") int moretimeSeconds) {
        this.gameRepo = gameRepo;
        this.messenger = messenger;
        this.ai = ai;
        this.finisher = finisher;
        this.moretimeSeconds = moretimeSeconds;
    }

    public List<Event> play(PovRef povRef, String fromString, String toString) {
        return play(povRef, fromString, toString, Optional.empty());
    }

    public List<Event> play(PovRef povRef, String origString, String destString, Optional<String> promString) {
        return fromPov(povRef, pov -> {
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

            List<Event> events = new ArrayList<>();
            if (progress.game().finished()) {
                gameRepo.save(progress);
                List<Event> finishEvents = finisher.moveFinish(progress.game(), color);
                events.addAll(progress.events());
                events.addAll(finishEvents);
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
                gameRepo.save(progress2);
                List<Event> finishEvents = finisher.moveFinish(progress2.game(), color.getOpposite());
                events.addAll(progress2.events());
                events.addAll(finishEvents);
            } else {
                gameRepo.save(progress);
                events.addAll(progress.events());
            }
            return events;
        });
    }

    public List<Event> abort(String fullId) {
        return attempt(fullId, finisher::abort);
    }

    public List<Event> resign(String fullId) {
        return attempt(fullId, finisher::resign);
    }

    public List<Event> outoftime(PovRef ref) {
        return attemptRef(ref, p -> finisher.outoftime(p.game()));
    }

    public List<Event> drawClaim(String fullId) {
        return attempt(fullId, finisher::drawClaim);
    }

    public List<Event> drawAccept(String fullId) {
        return attempt(fullId, finisher::drawAccept);
    }

    public List<Event> drawOffer(String fullId) {
        return fromPov(fullId, pov -> {
            DbGame g1 = pov.game();
            Color color = pov.color();
            if (g1.playerCanOfferDraw(color)) {
                if (g1.player(color.getOpposite()).getIsOfferingDraw()) return finisher.drawAccept(pov);

                List<Event> events = new ArrayList<>(List.of(new ReloadTableEvent(color.getOpposite())));
                events.addAll(messenger.systemMessages(g1, "Draw offer sent"));
                Progress p1 = new Progress(g1, events);
                g1.updatePlayer(color, p -> p.offerDraw(g1.getTurns()));
                gameRepo.save(p1);
                return p1.events();
            } else {
                throw new AppException("invalid draw offer " + fullId);
            }
        });
    }

    public List<Event> drawCancel(String fullId) {
        return fromPov(fullId, pov -> {
            DbGame g1 = pov.game();
            Color color = pov.color();
            if (pov.player().getIsOfferingDraw()) {
                List<Event> events = new ArrayList<>(List.of(new ReloadTableEvent(color.getOpposite())));
                events.addAll(messenger.systemMessages(g1, "Draw offer cancelled"));
                Progress p1 = new Progress(g1, events);
                g1.updatePlayer(color, DbPlayer::removeDrawOffer);
                gameRepo.save(p1);
                return p1.events();
            } else {
                throw new AppException("no draw offer to cancel " + fullId);
            }
        });
    }

    public List<Event> drawDecline(String fullId) {
        return fromPov(fullId, pov -> {
            DbGame g1 = pov.game();
            Color color = pov.color();
            if (g1.player(color.getOpposite()).getIsOfferingDraw()) {
                List<Event> events = new ArrayList<>(List.of(new ReloadTableEvent(color.getOpposite())));
                events.addAll(messenger.systemMessages(g1, "Draw offer declined"));
                Progress p1 = new Progress(g1, events);
                g1.updatePlayer(color, DbPlayer::removeDrawOffer);
                gameRepo.save(p1);
                return p1.events();
            } else {
                throw new AppException("no draw offer to decline " + fullId);
            }
        });
    }

    public List<Event> moretime(PovRef ref) {
        return attemptRef(ref, pov ->
            pov.game().getClock().filter(c -> pov.game().playable()).map(clock -> {
                Color color = pov.color().getOpposite();
                Clock newClock = clock.giveTime(color, moretimeSeconds);
                Progress progress = pov.game().withClock(newClock);
                List<Event> events = messenger.systemMessage(pov.game(), "%s + %d seconds".formatted(color, moretimeSeconds));
                progress.add(ClockEvent.apply(newClock));
                progress.addAll(events);
                gameRepo.save(progress);
                return progress.events();
        }).orElseThrow(() -> new AppException("cannot add more time")));
    }

    private <A> A attempt(String fullId, Function<Pov, A> action) {
        return fromPov(fullId, action);
    }

    private <A> A attemptRef(PovRef ref, Function<Pov, A> action) {
        return fromPov(ref, action);
    }

    private <A> A fromPov(String fullId, Function<Pov, A> op) {
        Pov pov = gameRepo.pov(fullId);
        return op.apply(pov);
    }

    private <A> A fromPov(PovRef ref, Function<Pov, A> op) {
        Pov pov = gameRepo.pov(ref);
        return op.apply(pov);
    }

}

package leo.lija.app.db;

import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.DbPlayer;
import leo.lija.app.entities.Pov;
import leo.lija.app.entities.PovRef;
import leo.lija.app.entities.Progress;
import leo.lija.app.entities.RawDbGame;
import leo.lija.app.entities.Status;
import leo.lija.chess.Color;
import leo.lija.chess.format.Fen;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static leo.lija.app.entities.DbGame.GAME_ID_SIZE;

@Repository
@RequiredArgsConstructor
public class GameRepo {

    private final GameRepoJpa repo;


    public Optional<DbGame> game(String gameId) {
        if (gameId.length() != GAME_ID_SIZE) return Optional.empty();
        return repo.findById(gameId).flatMap(RawDbGame::decode);
    }


    public Optional<DbPlayer> player(String gameId, Color color) {
        return game(gameId).map(g -> g.player(color));
    }

    public Optional<Pov> pov(String gameId, Color color) {
        return game(gameId).map(g -> Pov.apply(g, g.player(color)));
    }

    public Optional<Pov> pov(String fullId) {
        return game(fullId.substring(0, GAME_ID_SIZE)).flatMap(g ->
            g.player(fullId.substring(GAME_ID_SIZE)).map(p -> Pov.apply(g, p))
        );
    }

    public Optional<Pov> pov(PovRef ref) {
        return pov(ref.gameId(), ref.color());
    }

    public void save(DbGame game) {
        repo.save(encode(game));
    }

    public void save(Progress progress) {
        this.save(progress.game());
    }

    public Optional<String> insert(DbGame game) {
        return Optional.of(repo.save(encode(game)).getId()); // leo: probably we should check that id does not exist and else fail?
    }

    public Optional<DbGame> anyGame() {
        return repo.findAll(PageRequest.of(0, 1)).stream().findAny().flatMap(this::decode);
    }

    public void setEloDiffs(String id, int white, int black) {
        Optional<RawDbGame> game = repo.findById(id);
        game.ifPresent(g -> {
            g.getPlayers().get(0).setEloDiff(white);
            g.getPlayers().get(1).setEloDiff(black);
            repo.save(g);
        });
    }

    public void finish(String id, Optional<String> winnerId) {
        Optional<RawDbGame> game = repo.findById(id);
        game.ifPresent(g -> {
            g.setPositionHashes("");
            winnerId.ifPresent(g::setWinnerUserId);
            g.getPlayers().get(0).setLastDrawOffer(null);
            g.getPlayers().get(1).setLastDrawOffer(null);
            g.getPlayers().get(0).setIsOfferingDraw(null);
            g.getPlayers().get(1).setIsOfferingDraw(null);
            repo.save(g);
        });
    }

    public Optional<DbGame> decode(RawDbGame raw) {
        return raw.decode();
    }

    public RawDbGame encode(DbGame dbGame) {
        return RawDbGame.encode(dbGame);
    }

    public void saveInitialFen(DbGame dbGame) {
        repo.saveInitialFen(dbGame.getId(), Fen.obj2Str(dbGame.toChess()));
    }

    public void cleanupUnplayed() {
        repo.cleanupUnplayed(LocalDateTime.now().minusDays(2));
    }

    public List<DbGame> candidateToAutofinish() {
        return repo.candidatesToAutofinish(Status.STARTED.id(), LocalDateTime.now().minusHours(2)).stream()
            .map(RawDbGame::decode)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    public long countAll() {
        return repo.count();
    }
}

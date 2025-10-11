package leo.lija.system.db;

import leo.lija.chess.Color;
import leo.lija.chess.format.Fen;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.Pov;
import leo.lija.system.entities.RawDbGame;
import leo.lija.system.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

import static leo.lija.system.entities.DbGame.GAME_ID_SIZE;

@Repository
@RequiredArgsConstructor
public class GameRepo {

    private final GameRepoJpa repo;

    public Optional<RawDbGame> findById(String id) {
        return repo.findById(id);
    }

    public DbGame game(String gameId) {
        if (gameId.length() != GAME_ID_SIZE) {
            throw new AppException("Invalid game id " + gameId);
        }
        return findById(gameId).flatMap(this::decode).orElseThrow(() -> new AppException("No game found for id " + gameId));
    }

    public Pov pov(String gameId, Color color) {
        DbGame g = game(gameId);
        return Pov.apply(g, g.player(color));
    }

    public DbPlayer player(String gameId, Color color) {
        DbGame validGame = game(gameId);
        return validGame.player(color);
    }

    public Pov pov(String fullId) {
        DbGame g = game(fullId.substring(0, GAME_ID_SIZE));
        String playerId = fullId.substring(GAME_ID_SIZE);
        DbPlayer player = g.player(playerId).orElseThrow(() -> new AppException("No player found for id " + fullId));
        return Pov.apply(g, player);
    }

    public void save(DbGame game) {
        repo.save(encode(game));
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
            winnerId.ifPresent(userId -> g.setWinnerUserId(userId));
            g.getPlayers().get(0).setLastDrawOffer(null);
            g.getPlayers().get(1).setLastDrawOffer(null);
            g.getPlayers().get(0).setIsOfferingDraw(null);
            g.getPlayers().get(1).setIsOfferingDraw(null);
            g.getClock().setTimer(null);
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
}

package leo.lija.system.db;

import leo.lija.chess.Color;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.RawDbGame;
import leo.lija.system.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static leo.lija.system.entities.DbGame.GAME_ID_SIZE;

@Service
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

    public Pair<DbGame, DbPlayer> player(String gameId, Color color) {
        DbGame validGame = game(gameId);
        return Pair.of(validGame, validGame.player(color));
    }

    public DbPlayer playerOnly(String gameId, Color color) {
        DbGame validGame = game(gameId);
        return validGame.player(color);
    }

    public Pair<DbGame, DbPlayer> player(String fullId) {
        DbGame validGame = game(fullId.substring(0, GAME_ID_SIZE));
        String playerId = fullId.substring(GAME_ID_SIZE);
        DbPlayer player = validGame.player(playerId).orElseThrow(() -> new AppException("No player found for id " + fullId));
        return Pair.of(validGame, player);
    }

    public DbGame playerGame(String fullId) {
        Pair<DbGame, DbPlayer> gameAndPlayer = player(fullId);
        return gameAndPlayer.getFirst();
    }

    public void save(DbGame game) {
        if (game.getId() == null || !repo.existsById(game.getId())) return;
        repo.save(encode(game));
    }

    public Optional<String> insert(DbGame game) {
        return Optional.of(repo.save(encode(game)).getId()); // leo: probably we should check that id does not exist and else fail?
    }

    public Optional<DbGame> anyGame() {
        return repo.findAll(PageRequest.of(0, 1)).stream().findAny().flatMap(this::decode);
    }

    public Optional<DbGame> decode(RawDbGame raw) {
        return raw.decode();
    }

    public RawDbGame encode(DbGame dbGame) {
        return RawDbGame.encode(dbGame);
    }
}

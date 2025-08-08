package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.RawDbGame;
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

    public Optional<DbGame> game(String gameId) {
        if (gameId.length() == GAME_ID_SIZE) {
            return findById(gameId).flatMap(this::decode);
        }
        return Optional.empty();
    }

    public Optional<Pair<DbGame, DbPlayer>> player(String fullId) {
        return game(fullId.substring(0, GAME_ID_SIZE))
            .flatMap(g -> g.playerById(fullId.substring(GAME_ID_SIZE))
                .map(p -> Pair.of(g, p)));
    }

    public Optional<Pair<DbGame, DbPlayer>> player(String gameId, Color color) {
        return game(gameId.substring(0, GAME_ID_SIZE))
            .flatMap(g -> g.playerByColor(color)
                .map(p -> Pair.of(g, p)));
    }

    public void save(DbGame game) {
        if (game.getId() == null || !repo.existsById(game.getId())) return;
        repo.save(encode(game));
    }

    public Optional<String> insert(DbGame game) {
        return Optional.of(repo.save(encode(game)).getId()); // TODO: probably we should check that id does not exist and else fail?
    }

    public Optional<DbGame> anyGame() {
        return repo.findAll(PageRequest.of(0, 1)).stream().findAny().flatMap(this::decode);
    }

    private Optional<DbGame> decode(RawDbGame raw) {
        return raw.decode();
    }

    private RawDbGame encode(DbGame dbGame) {
        return RawDbGame.encode(dbGame);
    }
}

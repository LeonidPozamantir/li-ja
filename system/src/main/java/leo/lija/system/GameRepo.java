package leo.lija.system;

import leo.lija.system.entities.DbGame;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepo extends JpaRepository<DbGame, String> {

    public Optional<DbGame> findById(String id);

    public default Optional<DbGame> anyGame() {
        return findAll(PageRequest.of(0, 1)).stream().findAny();
    }
}

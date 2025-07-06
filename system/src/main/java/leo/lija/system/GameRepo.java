package leo.lija.system;

import leo.lija.system.entities.Game;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepo extends JpaRepository<Game, String> {

    public Optional<Game> findById(String id);

    public default Optional<Game> anyGame() {
        return findAll(PageRequest.of(0, 1)).stream().findAny();
    }
}

package leo.lija.system.repo;

import leo.lija.system.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepo extends JpaRepository<Game, String> {

    public Optional<Game> findById(String id);
}

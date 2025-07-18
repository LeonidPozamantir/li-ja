package leo.lija.system;

import leo.lija.system.entities.DbGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepoJpa extends JpaRepository<DbGame, String> {
    public Optional<DbGame> findById(String id);
}

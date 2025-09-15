package leo.lija.system.db;

import leo.lija.system.entities.RawDbGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepoJpa extends JpaRepository<RawDbGame, String> {
    public Optional<RawDbGame> findById(String id);
}

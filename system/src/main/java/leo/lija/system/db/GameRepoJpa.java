package leo.lija.system.db;

import jakarta.transaction.Transactional;
import leo.lija.system.entities.RawDbGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GameRepoJpa extends JpaRepository<RawDbGame, String> {
    Optional<RawDbGame> findById(String id);

    @Modifying
    @Query("update RawDbGame set initialFen=:initialFen where id=:id")
    @Transactional
    void saveInitialFen(String id, String initialFen);
}

package leo.lija.system.db;

import jakarta.transaction.Transactional;
import leo.lija.system.entities.RawDbGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameRepoJpa extends JpaRepository<RawDbGame, String> {
    Optional<RawDbGame> findById(String id);

    @Modifying
    @Query("update RawDbGame set initialFen=:initialFen where id=:id")
    @Transactional
    void saveInitialFen(String id, String initialFen);

    @Modifying
    @Query("delete from RawDbGame g where g.turns < 2 and g.createdAt < :until")
    @Transactional
    void cleanupUnplayed(LocalDateTime until);

    @Query("select g from RawDbGame g where g.clock is not null and g.status = :statusStarted and g.updatedAt < :until")
    List<RawDbGame> candidatesToAutofinish(int statusStarted, LocalDateTime until);
}

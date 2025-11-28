package leo.lija.app.db;

import leo.lija.app.entities.Hook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HookRepo extends JpaRepository<Hook, String> {

    Optional<Hook> findById(String id);

    Optional<Hook> findByOwnerId(String ownerId);

    @Query("select h from Hook h where h.match = false order by h.createdAt")
    List<Hook> allOpen();

    @Query("select h from Hook h where h.match = false and h.mode = 0 order by h.createdAt")
    List<Hook> allOpenCasual();

    @Transactional
    void deleteById(String id);

    @Transactional
    void deleteByOwnerId(String ownerId);

    @Query("select h from Hook h where h.ownerId not in :ids and h.match = false ")
    List<Hook> unmatchedNotInOwnerIds(Collection<String> ids);

    @Transactional
    default void cleanupOld() {
        cleanupByTime(LocalDateTime.now().minusHours(1));
    }

    @Modifying
    @Query("delete from Hook h where h.createdAt < :time")
    @Transactional
    void cleanupByTime(LocalDateTime time);

}

package leo.lija.system.db;

import leo.lija.system.entities.Hook;
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

    @Transactional
    default boolean keepOnlyOwnerIds(Collection<String> ids) {
        List<String> removableIds = getOtherIds(ids);
        if (!removableIds.isEmpty()) {
            deleteAllById(removableIds);
            return true;
        }
        return false;
    }

    @Query("select h.id from Hook h where h.ownerId not in :ids and h.match = false ")
    List<String> getOtherIds(Collection<String> ids);

    @Transactional
    default void cleanupOld() {
        cleanupByTime(LocalDateTime.now().minusHours(1));
    }

    @Modifying
    @Query("delete from Hook h where h.createdAt < :time")
    @Transactional
    void cleanupByTime(LocalDateTime time);

}

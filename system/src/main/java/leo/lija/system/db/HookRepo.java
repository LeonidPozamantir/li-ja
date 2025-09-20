package leo.lija.system.db;

import leo.lija.system.entities.Hook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface HookRepo extends JpaRepository<Hook, String> {

    public Optional<Hook> findById(String id);

    public Optional<Hook> findByOwnerId(String ownerId);

    @Query("select h from Hook h where h.match = false order by h.createdAt")
    public List<Hook> allOpen();

    @Query("select h from Hook h where h.match = false and h.mode = 0 order by h.createdAt")
    public List<Hook> allOpenCasual();

    @Transactional
    public void deleteById(String id);
}

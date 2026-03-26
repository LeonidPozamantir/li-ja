package leo.lija.app.db;

import jakarta.transaction.Transactional;
import leo.lija.app.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepoJpa extends JpaRepository<Message, Integer> {

    @Modifying
    @Transactional
    @Query("update Message set text='' where username=:username")
    void deleteByUsername(String username);
}

package leo.lija.system.db;

import leo.lija.system.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepoJpa extends JpaRepository<Message, Integer> {

}

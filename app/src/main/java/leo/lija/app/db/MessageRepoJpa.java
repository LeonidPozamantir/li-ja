package leo.lija.app.db;

import leo.lija.app.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepoJpa extends JpaRepository<Message, Integer> {

}

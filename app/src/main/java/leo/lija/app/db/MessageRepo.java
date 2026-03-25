package leo.lija.app.db;

import leo.lija.app.entities.Message;
import leo.lija.app.exceptions.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.regex.Pattern;

@Repository
public class MessageRepo extends CappedRepo<Message> {

    public MessageRepo(MessageRepoJpa repo, @Value("${lobby.message.max}") int max) {
        super(repo, max);
    }

    public Message add(Message message) {
        return repo.save(message);
    }

}

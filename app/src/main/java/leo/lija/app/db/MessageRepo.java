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

    private static final Pattern URL_REGEX = Pattern.compile("lija\\.com/([\\w-]{8})[\\w-]{4}");

    public Message add(String text, String username) {
        if (username.isEmpty() || username.equals("Anonymous")) throw new AppException("Invalid username " + username);
        int size = Math.min(140, text.length());
        String t = text.trim().substring(0, size);
        if (t.isEmpty()) throw new AppException("Empty message");
        String t1 = URL_REGEX.matcher(t).replaceAll(m -> "lija.com/" + m.group(1));
        return repo.save(new Message(null, username, t1));
    }

}

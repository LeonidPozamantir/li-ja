package leo.lija.system.db;

import leo.lija.system.entities.Message;
import leo.lija.system.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
public class MessageRepo {

    private final MessageRepoJpa repo;

    @Value("${lobby.message.max}")
    private int max;

    private static final Pattern URL_REGEX = Pattern.compile("lija\\.com/([\\w-]{8})[\\w-]{4}");

    public Message add(String text, String username) {
        if (username.isEmpty() || username.equals("Anonymous")) throw new AppException("Invalid username " + username);
        int size = Math.min(140, text.length());
        String t = text.trim().substring(0, size);
        if (t.isEmpty()) throw new AppException("Empty message");
        String t1 = URL_REGEX.matcher(t).replaceAll(m -> "lija.com/" + m.group(1));
        return repo.save(new Message(null, username, t1));
    }

    public List<Message> recent() {
        return repo.findAll(PageRequest.of(1, max, Sort.by(Sort.Direction.DESC, "id"))).toList();
    }
}

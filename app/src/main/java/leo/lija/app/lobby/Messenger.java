package leo.lija.app.lobby;

import leo.lija.app.db.MessageRepo;
import leo.lija.app.db.UserRepo;
import leo.lija.app.entities.Message;
import leo.lija.app.entities.User;
import leo.lija.app.exceptions.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class Messenger {

    private final MessageRepo messageRepo;
    private final UserRepo userRepo;

    private static final Pattern URL_REGEX = Pattern.compile("lija\\.com/([\\w-]{8})[\\w-]{4}");

    public Message apply(String text, String username) {
        Optional<User> userOption = userRepo.byUsername(username);
        return userOption.map(user -> {
            Message msg = createMessage(text, user);
            messageRepo.add(msg);
            return msg;
        }).orElseThrow(() -> new AppException("Unknown user"));
    }

    public Message createMessage(String text, User user) {
        if (user.isChatBan()) throw new AppException("Chat banned " + user);
        if (user.disabled()) throw new AppException("User disabled " + user);
        int size = Math.min(140, text.length());
        String t = text.trim().substring(0, size);
        if (t.isEmpty()) throw new AppException("Empty message");
        return new Message(null, user.getUsername(), URL_REGEX.matcher(t).replaceAll(m -> "lija.com/" + m.group(1)));
    }

    public void ban(String username) {
        Optional<User> userOption = userRepo.byUsername(username);
        userOption.ifPresent(user -> {
            userRepo.toggleChatBan(user);
            messageRepo.deleteByUsername(user.getUsername());
        });
    }
}

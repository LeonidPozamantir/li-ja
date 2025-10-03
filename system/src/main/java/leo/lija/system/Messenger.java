package leo.lija.system;

import leo.lija.chess.Color;
import leo.lija.system.db.RoomRepo;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.Room;
import leo.lija.system.entities.event.Event;
import leo.lija.system.entities.event.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Messenger {

    private final RoomRepo roomRepo;

    public void playerMessage(DbGame game, Color color, String message) {
        if (game.invited().isHuman() && message.length() <= 140 && !message.isEmpty()) {
            roomRepo.addMessage(game.getId(), color.getName(), message);
            game.withEvents(List.of(new MessageEvent(color.getName(), message)));
        }
    }

    public void systemMessages(DbGame game, String encodedMessages) {
        if (game.invited().isHuman()) {
            List<String> messages = Arrays.asList(encodedMessages.split("\\$"));
            roomRepo.addSystemMessages(game.getId(), messages);
            game.withEvents(messages.stream().map(msg -> (Event) new MessageEvent("system", msg)).toList());
        }
    }

    public void systemMessage(DbGame game, String message) {
        if (game.invited().isHuman()) {
            roomRepo.addSystemMessage(game.getId(), message);
            game.withEvents(List.of(new MessageEvent("system", message)));
        }
    }

    public List<Room.RoomMessage> render(String roomId) {
        return roomRepo.room(roomId).render();
    }
}

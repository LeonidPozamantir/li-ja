package leo.lija.app;

import leo.lija.chess.Color;
import leo.lija.app.db.RoomRepo;
import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.Room;
import leo.lija.app.entities.event.Event;
import leo.lija.app.entities.event.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Messenger {

    private final RoomRepo roomRepo;

    public List<Event> playerMessage(String gameId, Color color, String message) {
        if (message.length() <= 140 && !message.isEmpty()) {
            roomRepo.addMessage(gameId, color.getName(), message);
            return List.of(new MessageEvent(color.getName(), message));
        }
        return List.of();
    }

    public List<Event> systemMessages(DbGame game, String encodedMessages) {
        if (game.invited().isHuman()) {
            List<String> messages = Arrays.asList(encodedMessages.split("\\$"));
            roomRepo.addSystemMessages(game.getId(), messages);
            return messages.stream().map(msg -> (Event) new MessageEvent("system", msg)).toList();
        }
        return List.of();
    }

    public List<Event> systemMessage(DbGame game, String message) {
        if (game.invited().isHuman()) {
            roomRepo.addSystemMessage(game.getId(), message);
            return List.of(new MessageEvent("system", message));
        }
        return List.of();
    }

    public List<Room.RoomMessage> render(String roomId) {
        return roomRepo.room(roomId).render();
    }
}

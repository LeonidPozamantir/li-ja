package leo.lija.system.db;

import leo.lija.system.entities.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class RoomRepo {

    private final RoomRepoJpa repo;

    public Room room(String id) {
        return repo.findById(id).orElse(new Room(id, new ArrayList<>()));
    }

    public void addMessage(String id, String author, String message) {
        Room r = room(id);
        r.getMessages().add(Room.encode(author, message));
        repo.save(r);
    }

    public void addSystemMessage(String id, String message) {
        addMessage(id, "system", message);
    }

    public void addSystemMessages(String id, Collection<String> messages) {
        messages.forEach(message -> addSystemMessage(id, message));
    }
}

package leo.lija.system.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Room {

    @Id
    private String id;

    @ElementCollection
    private List<RoomMessage> messages;

    public List<RoomMessage> render() {
        return messages;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Embeddable
    public static class RoomMessage {
        private String author;
        private String message;
    }
}

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
    private List<Message> messages;

    public List<Message> render() {
        return messages;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Embeddable
    public static class Message {
        private String author;
        private String message;
    }
}

package leo.lija.system.entities;

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

    private List<String> messages;

    public List<RoomMessage> render() {
        return messages.stream().map(Room::decode).toList();
    }

    @AllArgsConstructor
    @Getter
    public static class RoomMessage {
        private String author;
        private String message;
    }

    public static String encode(String author, String message) {
        return switch (author) {
            case "white" -> "w";
            case "black" -> "b";
            default -> "s";
        } + message;
    }

    public static RoomMessage decode(String encoded) {
        return new RoomMessage(switch (encoded.charAt(0)) {
            case 'w' -> "white";
            case 'b' -> "black";
            default -> "system";
        }, encoded.substring(1));
    }
}

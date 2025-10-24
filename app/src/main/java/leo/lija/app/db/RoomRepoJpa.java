package leo.lija.app.db;

import leo.lija.app.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepoJpa extends JpaRepository<Room, String> {
}

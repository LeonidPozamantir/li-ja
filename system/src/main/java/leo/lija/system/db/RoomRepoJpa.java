package leo.lija.system.db;

import leo.lija.system.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepoJpa extends JpaRepository<Room, String> {
}

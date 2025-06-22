package leo.lija.http.repo;

import leo.lija.http.entities.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepo extends MongoRepository<Game, String> {
}

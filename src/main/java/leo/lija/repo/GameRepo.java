package leo.lija.repo;

import leo.lija.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameRepo extends MongoRepository<Game, String> {
}

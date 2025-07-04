package leo.lija.system;

import leo.lija.system.entities.Game;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("the game repo should")
class GameRepoTest {

    @Autowired
    private GameRepo repo;

    Game anyGame;

    @BeforeAll
    void init() {
        anyGame = repo.anyGame().get();
    }

    @Nested
    @DisplayName("find by ID")
    class findById {

        @Test
        @DisplayName("non-existing")
        void nonExisting() {
            assertThat(repo.findById("haha")).isEmpty();
        }

        @Test
        void existing() {
            Optional<Game> g = repo.findById(anyGame.getId());
            assertThat(g).isPresent();
            assertThat(g.get().getId()).isEqualTo(anyGame.getId());
        }
    }
}

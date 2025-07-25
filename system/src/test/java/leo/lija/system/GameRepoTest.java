package leo.lija.system;

import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("the game repo should")
class GameRepoTest extends Fixtures {

    @Autowired
    private GameRepo repo;

    @Autowired
    private GameRepoJpa jpa;

    DbGame anyGame;

    @BeforeAll
    void init() {
        jpa.save(newDbGameWithRandomIds());
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
            Optional<DbGame> g = repo.findById(anyGame.getId());
            assertThat(g).isPresent();
            assertThat(g.get().getId()).isEqualTo(anyGame.getId());
        }
    }

    @Nested
    @DisplayName("find a player")
    class FindPlayer {

        @Nested
        @DisplayName("by private ID")
        class PrivateId {
            @Test
            @DisplayName("non-existing")
            @Disabled("incorrect id; test will be deleted in May")
            void nonExisting() {
                assertThat(repo.player("huhu")).isEmpty();
            }

            @Test
            void existing() {
                DbPlayer player = anyGame.getPlayers().getFirst();
                assertThat(anyGame.fullIdOf(player).flatMap(repo::player).get().getSecond().getId()).isEqualTo(player.getId());
            }
        }

        @Nested
        @DisplayName("by ID and color")
        class IdColor {
            @Test
            @DisplayName("non-existing")
            void nonExisting() {
                assertThat(repo.player(anyGame.getId(),"huhu")).isEmpty();
            }

            @Test
            void existing() {
                DbPlayer player = anyGame.getPlayers().getFirst();
                assertThat(repo.player(anyGame.getId(), player.getColor()).get().getSecond().getId()).isEqualTo(player.getId());
            }
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("insert a new game")
    class NewGame {

        DbGame game = newDbGameWithRandomIds();

        @BeforeAll
        void init() {
            jpa.save(game);
        }

        @Test
        @DisplayName("find the saved game")
        void findSaved() {
            assertThat(repo.findById(game.getId())).isPresent();
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("update a game")
    class UpdateGame {

        DbGame game = newDbGameWithRandomIds();

        @BeforeAll
        void init() {
            jpa.save(game);
            game.setTurns(game.getTurns() + 1);
            repo.save(game);
        }

        @Test
        @DisplayName("find the updated game")
        @Disabled("clock is fetched incorrectly; test will be deleted in May")
        void findUpdated() {
            assertThat(repo.findById(game.getId())).contains(game);
        }
    }
}

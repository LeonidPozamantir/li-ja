package leo.lija.system;

import leo.lija.system.db.GameRepo;
import leo.lija.system.db.GameRepoJpa;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.RawDbGame;
import leo.lija.system.exceptions.AppException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import static leo.lija.chess.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("the game repo should")
class GameRepoTest extends Fixtures {

    @Autowired
    private GameRepo repo;
    @Autowired
    private GameRepoJpa repoJpa;

    DbGame anyGame;

    @BeforeAll
    void init() {
        repo.insert(newDbGameWithRandomIds());
        anyGame = repoJpa.findAll(PageRequest.of(0, 1)).stream().findAny().flatMap(RawDbGame::decode).get();
    }

    @Nested
    @DisplayName("find a game")
    class FindGame {

        @Nested
        @DisplayName("by ID")
        class ById {

            @Test
            @DisplayName("non-existing")
            void nonExisting() {
                assertThatThrownBy(() -> repo.game("haha")).isInstanceOf(AppException.class);
            }

            @Test
            void existing() {
                DbGame g = repo.game(anyGame.getId());
                assertThat(g.getId()).isEqualTo(anyGame.getId());
            }
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
            void nonExisting() {
                assertThatThrownBy(() -> repo.player("huhuhuhu")).isInstanceOf(AppException.class);
            }

            @Test
            void existing() {
                DbPlayer player = anyGame.players().getFirst();
                assertThat(repo.player(anyGame.fullIdOf(player).get()).getSecond().getId()).isEqualTo(player.getId());
            }
        }

        @Nested
        @DisplayName("by ID and color")
        class IdColor {
            @Test
            @DisplayName("non-existing")
            void nonExisting() {
                assertThatThrownBy(() -> repo.player("hahahaha",WHITE)).isInstanceOf(AppException.class);
            }

            @Test
            void existing() {
                DbPlayer player = anyGame.players().getFirst();
                assertThat(repo.player(anyGame.getId(), player.getColor()).getSecond().getId()).isEqualTo(player.getId());
            }
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("insert a new game")
    class NewGame {

        DbGame game = newDbGameWithRandomIds();

        @Test
        @DisplayName("find the saved game")
        void findSaved() {
            repo.insert(game);
            assertThat(repo.game(game.getId())).isEqualTo(game);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("update a game")
    class UpdateGame {

        DbGame game = newDbGameWithRandomIds();

        @Test
        @DisplayName("find the updated game")
        void findUpdated() {
            repo.insert(game);
            game.setTurns(game.getTurns() + 1);
            repo.save(game);
            assertThat(repo.game(game.getId())).isEqualTo(game);
        }
    }
}

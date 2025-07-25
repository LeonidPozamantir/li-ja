package leo.lija.system;

import leo.lija.chess.Pos;
import leo.lija.chess.format.VisualFormat;
import leo.lija.system.entities.DbGame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("the server should")
class ServerTest extends Fixtures {

    @Autowired
    private GameRepo repo;
    @Autowired
    private Server server;
    @Autowired GameRepoJpa repoJpa;

    private VisualFormat visualFormat = new VisualFormat();

    DbGame insert() {
        DbGame game = newDbGameWithRandomIds();
        repoJpa.save(game);
        return game;
    }
    Optional<Map<Pos, List<Pos>>> move(DbGame game, String m) {
        return game.playerByColor("white")
            .flatMap(player -> game.fullIdOf(player)
                .map(fullId -> server.playMove(fullId, m)));
    }
    Optional<Map<Pos, List<Pos>>> move(DbGame game) {
        return move(game, "d2 d4");
    }

    @Nested
    @DisplayName("play a single move")
    class PlaySingleMove {

        @Test
        @DisplayName("report success")
        void reportSuccess() {
            DbGame game = insert();
            assertThat(move(game)).isPresent();
        }

        @Nested
        @DisplayName("be persisted")
        class Persisted {
            @Test
            @DisplayName("update turns")
            void updateTurns() {
                DbGame game = insert();
                move(game);
                assertThat(repo.game(game.getId()).get().getTurns()).isEqualTo(1);
            }

            @Test
            @DisplayName("update board")
            void updateBoard() {
                DbGame game = insert();
                move(game);
                assertThat(visualFormat.newLine(repo.game(game.getId()).get().toChess().getBoard().visual())).isEqualTo("""
rnbqkbnr
pppppppp


   P

PPP PPPP
RNBQKBNR
""");
            }
        }
    }

    @Nested
    @DisplayName("play the Peruvian Immortal")
    class PeruvianImmortal {
        List<String> moves = List.of("e2 e4", "d7 d5", "e4 d5", "d8 d5", "b1 c3", "d5 a5", "d2 d4", "c7 c6", "g1 f3", "c8 g4", "c1 f4", "e7 e6", "h2 h3", "g4 f3", "d1 f3", "f8 b4", "f1 e2", "b8 d7", "a2 a3", "e8 c8", "a3 b4", "a5 a1", "e1 d2", "a1 h1", "f3 c6", "b7 c6", "e2 a6");

        List<Map<Pos, List<Pos>>> play(DbGame game) {
            return moves.stream().map(m -> move(game, m).get()).toList();
        }

        @Test
        @DisplayName("report success")
        void reportSuccess() {
            DbGame game = insert();
            assertThat(play(game)).size().isGreaterThan(0);
        }

        @Nested
        @DisplayName("be persisted")
        class Persisted {
            @Test
            @DisplayName("update turns")
            void updateTurns() {
                DbGame game = insert();
                play(game);
                assertThat(repo.game(game.getId()).get().getTurns()).isEqualTo(27);
            }

            @Test
            @DisplayName("update board")
            void updateBoard() {
                DbGame game = insert();
                play(game);
                assertThat(visualFormat.newLine(repo.game(game.getId()).get().toChess().getBoard().visual())).isEqualTo("""
  kr  nr
p  n ppp
B p p

 P P B
  N    P
 PPK PP
       q
""");
            }
        }
    }


}

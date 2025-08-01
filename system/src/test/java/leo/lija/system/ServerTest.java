package leo.lija.system;

import leo.lija.chess.Pos;
import leo.lija.chess.exceptions.ChessRulesException;
import leo.lija.chess.format.VisualFormat;
import leo.lija.chess.utils.Pair;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.DbPlayer;
import leo.lija.system.entities.EventStack;
import leo.lija.system.entities.event.Event;
import leo.lija.system.entities.event.ThreefoldEvent;
import leo.lija.system.exceptions.AppException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        return insert(newDbGameWithRandomIds());
    }
    DbGame insert(DbGame dbGame) {
        repoJpa.save(dbGame);
        return dbGame;
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
        @DisplayName("wrong player")
        void wrongPlayer() {
            DbGame game = insert();
            assertThatThrownBy(() -> move(game, "d7 d5")).isInstanceOf(ChessRulesException.class);
        }

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
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Persisted {
            DbGame game;
            Optional<DbGame> found;

            @BeforeAll
            void init() {
                game = insert();
                play(game);
                found = repo.game(game.getId());
            }

            @Test
            @DisplayName("update turns")
            void updateTurns() {
                assertThat(found.get().getTurns()).isEqualTo(27);
            }

            @Test
            @DisplayName("update board")
            void updateBoard() {
                assertThat(visualFormat.newLine(found.get().toChess().getBoard().visual())).isEqualTo("""
  kr  nr
p  n ppp
B p p

 P P B
  N    P
 PPK PP
       q
""");
            }

            @Nested
            @DisplayName("event stacks")
            class EventStacks {
                Optional<EventStack> stack = found.flatMap(g -> g.playerByColor("white")).map(DbPlayer::eventStack);

                @Test
                @DisplayName("high version number")
                void highVersionNumber() {
                    assertThat(stack.get().version()).isGreaterThan(20);
                }

                @Test
                void rotated() {
                    assertThat(stack.get().getEvents().size()).isEqualTo(16);
                }
            }
        }
    }

    @Nested
    @DisplayName("play to threefold repetition")
    class PlayToThreefoldRepetition {
        List<String> moves = List.of("b1 c3", "b8 c6", "c3 b1", "c6 b8", "b1 c3", "b8 c6", "c3 b1", "c6 b8", "b1 c3", "b8 c6");

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
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Persisted {
            DbGame game;
            Optional<DbGame> found;
            Optional<List<Pair<Integer, Event>>> events;

            @BeforeAll
            void init() {
                game = insert();
                play(game);
                found = repo.game(game.getId());
                events = found.flatMap(g -> g.playerByColor("white").map(p -> p.eventStack().getEvents()));
            }

            @Test
            @DisplayName("propose threefold")
            void proposeThreefold() {
                assertThat(events.get().stream().map(Pair::getSecond).toList()).contains(new ThreefoldEvent());
            }
        }
    }

    @Test
    @DisplayName("play on playing game")
    void playOnPlayingGame() {
        DbGame dbGame = insert(randomizeIds(newDbGameWithBoard(visualFormat.str2Obj("""
PP kr
K
"""))));
        assertThat(move(dbGame, "a1 b1")).isPresent();
    }

    @Nested
    @DisplayName("play on finished game")
    class PlayOnFinishedGame {

        @Test
        @DisplayName("by checkmate")
        void byCheckmate() {
            DbGame dbGame = insert(randomizeIds(newDbGameWithBoard(visualFormat.str2Obj("""
PP
K  r
"""))));
            assertThatThrownBy(() -> move(dbGame, "a1 b1")).isInstanceOf(ChessRulesException.class);
        }

        @Test
        @DisplayName("by autodraw")
        void byAutodraw() {
            DbGame dbGame = insert(randomizeIds(newDbGameWithBoard(visualFormat.str2Obj("""
      k
K     B"""))));
            assertThatThrownBy(() -> move(dbGame, "a1 b1")).isInstanceOf(ChessRulesException.class);
        }
    }

}

package leo.lija.system;

import leo.lija.chess.Pos;
import leo.lija.chess.exceptions.ChessRulesException;
import leo.lija.chess.format.VisualFormat;
import leo.lija.system.entities.DbGame;
import leo.lija.system.entities.event.ThreefoldEvent;
import leo.lija.system.exceptions.AppException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static leo.lija.chess.Color.WHITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DisplayName("the server should")
class ServerTest extends Fixtures {

    @Autowired
    private GameRepo repo;
    @Autowired
    private Server server;

    private VisualFormat visualFormat = new VisualFormat();

    DbGame insert() {
        return insert(newDbGameWithRandomIds());
    }
    DbGame insert(DbGame dbGame) {
        repo.insert(dbGame);
        return dbGame;
    }

    void move(DbGame game, String m) {
        server.playMove(game.fullIdOf(WHITE), m);
    }
    void move(DbGame game) {
        move(game, "d2 d4");
    }

    DbGame updated() {
        return updated(newDbGameWithRandomIds(), "d2 d4");
    }

    DbGame updated(DbGame game, String m) {
        DbGame inserted = insert(game);
        move(inserted, m);
        return repo.game(game.getId());
    }

    DbGame play(DbGame game, List<String> ms) {
        return ms.stream().reduce(game, (g, m) -> updated(g, m), (g1, g2) -> g1);
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
            assertThatCode(() -> move(game)).doesNotThrowAnyException();
        }

        @Nested
        @DisplayName("be persisted")
        class Persisted {
            @Test
            @DisplayName("update turns")
            void updateTurns() {
                DbGame game = updated();
                assertThat(game.getTurns()).isEqualTo(1);
            }

            @Test
            @DisplayName("update board")
            void updateBoard() {
                DbGame game = updated();
                assertThat(visualFormat.newLine(game.toChess().getBoard().visual())).isEqualTo("""
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

        @Test
        @DisplayName("report success")
        void reportSuccess() {
            assertThatCode(() -> play(insert(), moves)).doesNotThrowAnyException();
        }

        @Nested
        @DisplayName("be persisted")
        class Persisted {

            DbGame found() {
                DbGame game = play(insert(), moves);
                return repo.game(game.getId());
            }

            @Test
            @DisplayName("update turns")
            void updateTurns() {
                assertThat(found().getTurns()).isEqualTo(27);
            }

            @Test
            @DisplayName("update board")
            void updateBoard() {
                assertThat(visualFormat.newLine(found().toChess().getBoard().visual())).isEqualTo("""
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

                @Test
                @DisplayName("high version number")
                void highVersionNumber() {
                    assertThat(found().player(WHITE).eventStack().lastVersion()).isGreaterThan(20);
                }

                @Test
                void rotated() {
                    assertThat(found().player(WHITE).eventStack().getEvents().size()).isEqualTo(16);
                }
            }
        }
    }

    @Nested
    @DisplayName("play to threefold repetition")
    class PlayToThreefoldRepetition {
        List<String> moves = List.of("b1 c3", "b8 c6", "c3 b1", "c6 b8", "b1 c3", "b8 c6", "c3 b1", "c6 b8", "b1 c3", "b8 c6");

        @Test
        @DisplayName("report success")
        void reportSuccess() {
            assertThat(play(insert(), moves).player(WHITE).eventStack().getEvents()).anyMatch(p -> p.getSecond().equals(new ThreefoldEvent()));
        }

        @Nested
        @DisplayName("be persisted")
        class Persisted {

            DbGame found() {
                DbGame game = play(insert(), moves);
                return repo.game(game.getId());
            }

            @Test
            @DisplayName("propose threefold")
            void proposeThreefold() {
                assertThat(found().player(WHITE).eventStack().getEvents()).anyMatch(p -> p.getSecond().equals(new ThreefoldEvent()));
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
        assertThatCode(() -> play(dbGame, List.of("a1 b1"))).doesNotThrowAnyException();
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
            var movesList = List.of("a1 b1");
            assertThatThrownBy(() -> play(dbGame, movesList)).isInstanceOf(AppException.class);
        }

        @Test
        @DisplayName("by autodraw")
        void byAutodraw() {
            DbGame dbGame = insert(randomizeIds(newDbGameWithBoard(visualFormat.str2Obj("""
      k
K     B"""))));
            var movesList = List.of("a1 b1");
            assertThatThrownBy(() -> play(dbGame, movesList)).isInstanceOf(AppException.class);
        }
    }

}

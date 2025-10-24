package leo.lija.app;

import leo.lija.app.entities.DbGame;
import leo.lija.app.entities.event.CheckEvent;
import leo.lija.app.entities.event.EnpassantEvent;
import leo.lija.app.entities.event.Event;
import leo.lija.app.entities.event.MoveEvent;
import leo.lija.app.entities.event.PossibleMovesEvent;
import leo.lija.chess.Color;
import leo.lija.chess.Game;
import leo.lija.chess.format.VisualFormat;
import leo.lija.chess.utils.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static leo.lija.chess.Color.BLACK;
import static leo.lija.chess.Color.WHITE;
import static leo.lija.chess.Pos.A2;
import static leo.lija.chess.Pos.A3;
import static leo.lija.chess.Pos.A5;
import static leo.lija.chess.Pos.A6;
import static leo.lija.chess.Pos.A7;
import static leo.lija.chess.Pos.B5;
import static leo.lija.chess.Pos.B6;
import static leo.lija.chess.Pos.B7;
import static leo.lija.chess.Pos.B8;
import static leo.lija.chess.Pos.C3;
import static leo.lija.chess.Pos.C5;
import static leo.lija.chess.Pos.C6;
import static leo.lija.chess.Pos.C7;
import static leo.lija.chess.Pos.D2;
import static leo.lija.chess.Pos.D4;
import static leo.lija.chess.Pos.D5;
import static leo.lija.chess.Pos.D6;
import static leo.lija.chess.Pos.D7;
import static leo.lija.chess.Pos.E1;
import static leo.lija.chess.Pos.E4;
import static leo.lija.chess.Pos.E5;
import static leo.lija.chess.Pos.E6;
import static leo.lija.chess.Pos.E7;
import static leo.lija.chess.Pos.F5;
import static leo.lija.chess.Pos.F6;
import static leo.lija.chess.Pos.F7;
import static leo.lija.chess.Pos.G5;
import static leo.lija.chess.Pos.G6;
import static leo.lija.chess.Pos.G7;
import static leo.lija.chess.Pos.G8;
import static leo.lija.chess.Pos.H1;
import static leo.lija.chess.Pos.H5;
import static leo.lija.chess.Pos.H6;
import static leo.lija.chess.Pos.H7;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("chess to model conversion")
class ChessToModelTest extends Fixtures {

    private final VisualFormat visual = new VisualFormat();

    @Nested
    @DisplayName("new game")
    class newGame {
        DbGame dbGame = newDbGame;
        Game game = dbGame.toChess();

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Identity {
            DbGame dbg2 = dbGame.copy();

            @BeforeAll
            void init() {
                dbg2.update(game, anyMove);
            }

            @Test
            @DisplayName("white pieces")
            void whitePieces() {
                assertThat(sortPs(dbg2.player(WHITE).getPs())).isEqualTo(
                    sortPs(dbGame.player(WHITE).getPs())
                );
            }

            @Test
            @DisplayName("black pieces")
            void blackPieces() {
                assertThat(sortPs(dbg2.player(BLACK).getPs())).isEqualTo(
                    sortPs(dbGame.player(BLACK).getPs())
                );
            }
        }
    }

    @Nested
    @DisplayName("played game")
    class PlayedGame {
        DbGame dbGame = dbGame4;
        Game game = new Game(visual.str2Obj("""
r   kb r
ppp pppp
  np
    P
   P   N
  P B P
P P  P P
R  QK  q
"""), WHITE, dbGame.getPgn(), Optional.empty(), io.vavr.collection.List.of(
            Pair.of(C3, BLACK.knight()),
            Pair.of(F5, BLACK.bishop()),
            Pair.of(F5, WHITE.bishop()),
            Pair.of(C3, WHITE.knight()),
            Pair.of(H1, WHITE.rook())
        ));

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Identity {
            DbGame dbg2;

            @BeforeAll
            void setup() {
                dbg2 = dbGame.copy();
                dbg2.update(game, anyMove);
            }

            @Test
            void white() {
                assertThat(sortPs(dbg2.player(WHITE).getPs())).isEqualTo(
                    sortPs(dbGame.player(WHITE).getPs())
                );
            }

            @Test
            void black() {
                assertThat(sortPs(dbg2.player(BLACK).getPs())).isEqualTo(
                    sortPs(dbGame.player(BLACK).getPs())
                );
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @DisplayName("new pieces positions")
        class NewPositions {
            DbGame dbg2;

            @BeforeAll
            void setup() {
                dbg2 = newDbGame.copy();
                dbg2.update(game, anyMove);
            }

            @Test
            void white() {
                assertThat(sortPs(dbg2.player(WHITE).getPs())).isEqualTo(
                    sortPs(dbGame.player(WHITE).getPs())
                );
            }

            @Test
            void black() {
                assertThat(sortPs(dbg2.player(BLACK).getPs())).isEqualTo(
                    sortPs(dbGame.player(BLACK).getPs())
                );
            }
        }
    }

    @Nested
    @DisplayName("update events")
    class UpdateEvents {

        private Optional<List<Pair<Integer, Event>>> playerEvents(DbGame dbg, Color color) {
            return Optional.of(dbg).map(g -> g.player(color).eventStack().getEvents());
        }

        @Nested
        @DisplayName("simple move")
        class SimpleMove {
            DbGame dbg = new RichDbGame(newDbGame).withoutEvents().afterMove(D2, D4);

            @Test
            @DisplayName("white events")
            void whiteEvents() {
                assertThat(playerEvents(dbg, WHITE).get()).containsExactly(
                    Pair.of(1, new MoveEvent(D2, D4, WHITE)),
                    Pair.of(2, new PossibleMovesEvent(Map.of()))
                );
            }

            @Test
            @DisplayName("black events")
            void blackEvents() {
                assertThat(playerEvents(dbg, BLACK).get()).containsExactly(
                    Pair.of(1, new MoveEvent(D2, D4, WHITE)),
                    Pair.of(2, new PossibleMovesEvent(Map.of(G7, List.of(G6, G5), F7, List.of(F6, F5), D7, List.of(D6, D5), A7, List.of(A6, A5), G8, List.of(F6, H6), C7, List.of(C6, C5), B8, List.of(A6, C6), B7, List.of(B6, B5), H7, List.of(H6, H5), E7, List.of(E6, E5))))
                );
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Check {

            RichDbGame dbg;

            @BeforeAll
            void init() {
                dbg = new RichDbGame(newDbGameWithBoard(visual.str2Obj("""
   r

PPPP   P
RNBQK  R
""")));
                dbg.setTurns(11);
                dbg.withoutEvents().afterMove(D4, E4);
            }

            @Test
            @DisplayName("white events")
            void whiteEvents() {
                assertThat(playerEvents(dbg, WHITE).get().stream().map(Pair::getSecond).toList()).contains(new CheckEvent(E1));
            }

            @Test
            @DisplayName("black events")
            void blackEvents() {
                assertThat(playerEvents(dbg, BLACK).get().stream().map(Pair::getSecond).toList()).contains(new CheckEvent(E1));
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Checkmate {

            RichDbGame dbg;

            @BeforeAll
            void init() {
                dbg = new RichDbGame(newDbGameWithBoard(visual.str2Obj("""
   r

PPPP P P
RNBRKR R
""")));
                dbg.setTurns(11);
                dbg.withoutEvents().afterMove(D4, E4);
            }

            @Test
            @DisplayName("white events")
            void whiteEvents() {
                assertThat(playerEvents(dbg, WHITE).get().stream().map(Pair::getSecond).toList()).contains(new CheckEvent(E1));
            }

            @Test
            @DisplayName("black events")
            void blackEvents() {
                assertThat(playerEvents(dbg, BLACK).get().stream().map(Pair::getSecond).toList()).contains(new CheckEvent(E1));
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Stalemate {

            RichDbGame dbg;

            @BeforeAll
            void init() {
                dbg = new RichDbGame(newDbGameWithBoard(visual.str2Obj("""
p
 rr
K
""")));
                dbg.setTurns(11);
                dbg.withoutEvents().afterMove(A3, A2);
            }

            @Test
            @DisplayName("white events")
            void whiteEvents() {
                assertThat(playerEvents(dbg, WHITE).get().stream().map(Pair::getSecond).toList()).contains(new PossibleMovesEvent(Map.of()));
            }

            @Test
            @DisplayName("black events")
            void blackEvents() {
                assertThat(playerEvents(dbg, BLACK).get().stream().map(Pair::getSecond).toList()).contains(new PossibleMovesEvent(Map.of()));
            }
        }

        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class Enpassant {

            RichDbGame dbg;

            @BeforeAll
            void init() {
                dbg = new RichDbGame(newDbGameWithBoard(visual.str2Obj("""
  Pp


PP P P P
RNBRKR R
""")));
                dbg.setTurns(10);
                dbg.setLastMove(Optional.of("d7 d5"));
                dbg.withoutEvents().afterMove(C5, D6);
            }

            @Test
            @DisplayName("white events")
            void whiteEvents() {
                assertThat(playerEvents(dbg, WHITE).get().stream().map(Pair::getSecond).toList()).contains(new EnpassantEvent(D5));
            }

            @Test
            @DisplayName("black events")
            void blackEvents() {
                assertThat(playerEvents(dbg, BLACK).get().stream().map(Pair::getSecond).toList()).contains(new EnpassantEvent(D5));
            }

        }
    }

    private String sortPs(String ps) {
        return Arrays.stream(ps.split(" ")).sorted().collect(Collectors.joining(" "));
    }
}
